begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|benchmark
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_comment
comment|/**  * Enumerates some Calendar with math applied for easying tests  */
end_comment

begin_enum
specifier|public
enum|enum
name|Date
block|{
comment|/**      * what could be considered the current timestamp      */
name|NOW
argument_list|(
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
block|,
comment|/**      * given {@code NOW} less 2 hours      */
name|LAST_2_HRS
argument_list|(
name|add
argument_list|(
name|NOW
operator|.
name|getCalendar
argument_list|()
argument_list|,
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
operator|-
literal|2
argument_list|)
argument_list|)
block|,
comment|/**      * given {@code NOW} less 24 hours      */
name|LAST_24_HRS
argument_list|(
name|add
argument_list|(
name|NOW
operator|.
name|getCalendar
argument_list|()
argument_list|,
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
operator|-
literal|24
argument_list|)
argument_list|)
block|,
comment|/**      * given {@code NOW} less 1 week      */
name|LAST_7_DAYS
argument_list|(
name|add
argument_list|(
name|NOW
operator|.
name|getCalendar
argument_list|()
argument_list|,
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
operator|-
literal|24
operator|*
literal|7
argument_list|)
argument_list|)
block|,
comment|/**      * given {@code NOW} less 1 month      */
name|LAST_MONTH
argument_list|(
name|add
argument_list|(
name|NOW
operator|.
name|getCalendar
argument_list|()
argument_list|,
name|Calendar
operator|.
name|MONTH
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
block|,
comment|/**      * given {@code NOW} less 1 year      */
name|LAST_YEAR
argument_list|(
name|add
argument_list|(
name|NOW
operator|.
name|getCalendar
argument_list|()
argument_list|,
name|Calendar
operator|.
name|YEAR
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
block|;
comment|/**      * perform math on the provided calendar and return it.      *       * @param cal      * @param field      * @param amount      * @return      */
specifier|private
specifier|static
name|Calendar
name|add
parameter_list|(
name|Calendar
name|cal
parameter_list|,
name|int
name|field
parameter_list|,
name|int
name|amount
parameter_list|)
block|{
name|cal
operator|.
name|add
argument_list|(
name|field
argument_list|,
name|amount
argument_list|)
expr_stmt|;
return|return
name|cal
return|;
block|}
specifier|private
specifier|final
name|Calendar
name|cal
decl_stmt|;
name|Date
parameter_list|(
name|Calendar
name|cal
parameter_list|)
block|{
name|this
operator|.
name|cal
operator|=
name|cal
expr_stmt|;
block|}
specifier|public
name|Calendar
name|getCalendar
parameter_list|()
block|{
comment|// duplicating the calendar for allowing safe operations from consumers
name|Calendar
name|c
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|c
operator|.
name|setTime
argument_list|(
name|cal
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|Date
argument_list|>
name|VALUES
init|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|SIZE
init|=
name|VALUES
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Random
name|RND
init|=
operator|new
name|Random
argument_list|(
literal|30
argument_list|)
decl_stmt|;
comment|/**      * return a random Date      *       * @return      */
specifier|public
specifier|static
name|Date
name|randomDate
parameter_list|()
block|{
return|return
name|VALUES
operator|.
name|get
argument_list|(
name|RND
operator|.
name|nextInt
argument_list|(
name|SIZE
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|String
name|toISO_8601_2000
parameter_list|()
block|{
name|SimpleDateFormat
name|format
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
argument_list|)
decl_stmt|;
name|format
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|format
operator|.
name|format
argument_list|(
name|getCalendar
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
block|}
end_enum

end_unit

