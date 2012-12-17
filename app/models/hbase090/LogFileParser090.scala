package models.hbase090

import models.LogFile
import java.util.regex._
import java.util.Date

class LogFileParser090 extends models.LogFileParser {

  val COMPACTION = Pattern.compile(
    """^(.*) INFO (.*).HRegion: completed compaction on region (.*\.) after (.*)$""",
    Pattern.MULTILINE
  )
  val DATE_GROUP = 1
  val REGION_GROUP = 3
  val DURATION_GROUP = 4

  override def eachCompaction(logFile: LogFile, functionBlock: (String, Date, Long) => Unit) = {
    val m = COMPACTION.matcher(logFile.tail())
    while(m.find()) {
      val date = m.group(DATE_GROUP)
      val region = m.group(REGION_GROUP)
      val duration = m.group(DURATION_GROUP)

      val end = LogFile.dateFormat.parse(date)
      val durationMsec = parseDuration(duration)

      functionBlock(region, end, durationMsec)
    }
  }

}