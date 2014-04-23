/*
 *  Copyright 2010, 2011 Christopher Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.usertype.dateandtime.joda.columnmapper;

import java.sql.Types;
import java.util.Calendar;

import org.jadira.usertype.dateandtime.joda.util.ZoneHelper;
import org.jadira.usertype.spi.shared.AbstractColumnMapper;
import org.jadira.usertype.spi.shared.ColumnMapper;
import org.jadira.usertype.spi.shared.DatabaseZoneConfigured;
import org.jadira.usertype.spi.shared.DstSafeDateTimeType;
import org.jadira.usertype.spi.shared.JavaZoneConfigured;
import org.jadira.usertype.spi.shared.VersionableColumnMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

/**
 * Maps a precise datetime column for storage. The UTC Zone will be used to store the value
 */
public class TimestampColumnDateTimeMapper extends AbstractColumnMapper<DateTime, DateTime> implements ColumnMapper<DateTime, DateTime>, DatabaseZoneConfigured<DateTimeZone>, JavaZoneConfigured<DateTimeZone>, VersionableColumnMapper<DateTime, DateTime> {

    private static final long serialVersionUID = -7670411089210984705L;

    public static final DateTimeFormatter DATETIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss'.'").appendFractionOfSecond(0, 9).toFormatter();

    private DateTimeZone databaseZone = DateTimeZone.UTC;

    private DateTimeZone javaZone = null;

    public TimestampColumnDateTimeMapper() {
    }
    
    public TimestampColumnDateTimeMapper(DateTimeZone databaseZone, DateTimeZone javaZone) {
    	this.databaseZone = databaseZone;
    	this.javaZone = javaZone;
    }
    
    @Override
    public final int getSqlType() {
        return Types.TIMESTAMP;
    }
    
    @Override
    public DateTime generateCurrentValue() {
        return new DateTime(System.currentTimeMillis());
    }
    
    @Override
    public DateTime fromNonNullString(String s) {
        return new DateTime(s);
    }

    @Override
    public DateTime fromNonNullValue(DateTime value) {

        DateTimeZone currentJavaZone = javaZone == null ? ZoneHelper.getDefault() : javaZone;
        DateTime dateTimeWithZone = value.withZone(currentJavaZone);
        
        return dateTimeWithZone;
    }

    @Override
    public String toNonNullString(DateTime value) {
        return value.toString();
    }

    @Override
    public DateTime toNonNullValue(DateTime value) {

        return value;
    }

    @Override
	public void setDatabaseZone(DateTimeZone databaseZone) {
        this.databaseZone = databaseZone;
    }

    @Override
    public void setJavaZone(DateTimeZone javaZone) {
        this.javaZone = javaZone;
    }
    
	@Override
	public DateTimeZone parseZone(String zoneString) {
		return DateTimeZone.forID(zoneString);
	}
	
    @Override
    public final DstSafeDateTimeType getHibernateType() {
    	return databaseZone == null ? DstSafeDateTimeType.INSTANCE : new DstSafeDateTimeType(Calendar.getInstance(databaseZone.toTimeZone()));
    }
}
