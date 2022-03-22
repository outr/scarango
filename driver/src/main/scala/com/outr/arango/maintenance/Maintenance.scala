package com.outr.arango.maintenance

import cats.effect.IO
import cats.effect.unsafe.implicits.global

import java.util.{Calendar, TimeZone}
import scala.concurrent.duration._

object Maintenance {
  def schedule(schedule: FiniteDuration,
               initialDelay: Option[FiniteDuration] = None)
              (action: TaskStatus => IO[TaskResult]): MaintenanceTask = {
    var normalSchedule = schedule
    var stat = TaskStatus().schedule(initialDelay.getOrElse(schedule))
    var cancelled = false
    val task = new MaintenanceTask {
      override def status: TaskStatus = stat

      override def cancel(): Unit = cancelled = true
    }

    def scheduleNext(resultOption: Option[TaskResult]): IO[TaskResult] = {
      stat = stat.copy(lastRun = Some(System.currentTimeMillis()), timesRun = stat.timesRun + 1)
      if (cancelled) {
        stat = stat.copy(nextRun = None, nextSchedule = None)
        IO.pure(TaskResult.Stop)
      } else {
        val nextRunOption = resultOption match {
          case None => Some(initialDelay.getOrElse(schedule))
          case Some(TaskResult.Continue) => Some(normalSchedule)
          case Some(TaskResult.Stop) => None
          case Some(TaskResult.RunAgain) => Some(0.seconds)
          case Some(TaskResult.ChangeSchedule(delay)) =>
            normalSchedule = delay
            Some(delay)
          case Some(TaskResult.NextSchedule(delay)) => Some(delay)
        }
        nextRunOption match {
          case Some(nextRun) =>
            stat = stat.schedule(nextRun)
            IO.sleep(nextRun).flatMap { _ =>
              val io = action(stat)
              io.flatMap { result =>
                scheduleNext(Some(result))
              }
            }
          case None =>
            stat = stat.copy(nextRun = None, nextSchedule = None)
            IO.pure(TaskResult.Stop)
        }
      }
    }

    scheduleNext(None).unsafeRunAndForget()

    task
  }

  def fromTime(hour: Int,
               minute: Int = 0,
               second: Int = 0,
               millisecond: Int = 0,
               rollToNextDay: Boolean = true,
               timeZone: TimeZone = TimeZone.getDefault): FiniteDuration = {
    val c = Calendar.getInstance(timeZone)
    c.set(Calendar.HOUR_OF_DAY, hour)
    c.set(Calendar.MINUTE, minute)
    c.set(Calendar.SECOND, second)
    c.set(Calendar.MILLISECOND, millisecond)
    val l = c.getTimeInMillis - System.currentTimeMillis()
    if (l <= 0 && rollToNextDay) {
      (l + 24.hours.toMillis).millis
    } else {
      l.millis
    }
  }
}
