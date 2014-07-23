select tc.testId,tc.studentId,tc.executionDate,tc.type,rtc.testId,rtc.studentId,rtc.executionDate,rtc.soundFileUrl,rtc.professorObservations,rtc.wordsPerMinute,rtc.correctWordCount,rtc.readingPrecision,rtc.readingSpeed,rtc.expressiveness,rtc.rhythm,rtc.details,rtc.wasCorrected from TestCorrections as tc join ReadingTestCorrections as rtc on tc.testId = rtc.testId and tc.studentId = rtc.studentId and tc.executionDate = rtc.executionDate;

select tc.testId,tc.studentId,tc.executionDate,tc.type from TestCorrections as tc, ReadingTestCorrections as rtc where tc.testId = rtc.testId and tc.studentId = rtc.studentId and tc.executionDate = rtc.executionDate;