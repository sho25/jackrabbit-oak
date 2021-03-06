begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|plugins
operator|.
name|memory
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Calendar
operator|.
name|HOUR_OF_DAY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|memory
operator|.
name|PropertyValues
operator|.
name|newDate
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|util
operator|.
name|ISO8601
operator|.
name|format
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|TimeZone
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|PropertyValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|PropertyStateValueTest
block|{
comment|/*      * GMT      */
specifier|private
specifier|static
specifier|final
name|TimeZone
name|GMT
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
decl_stmt|;
comment|/*      * GMT +1      */
specifier|private
specifier|static
specifier|final
name|TimeZone
name|CET
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"CET"
argument_list|)
decl_stmt|;
comment|/*      * GMT -8      */
specifier|private
specifier|static
specifier|final
name|TimeZone
name|PST
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"PST"
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|compareDates
parameter_list|()
block|{
name|Calendar
name|d1
decl_stmt|,
name|d2
decl_stmt|;
name|PropertyValue
name|v1
decl_stmt|,
name|v2
decl_stmt|;
comment|// same time zones.
name|d1
operator|=
name|newCal
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|d2
operator|=
name|newCal
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|v1
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d1
argument_list|)
argument_list|)
expr_stmt|;
name|v2
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v1 and v2 should be equals"
argument_list|,
literal|0
argument_list|,
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v1 and v2 should be equals"
argument_list|,
literal|0
argument_list|,
name|v2
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|=
name|newCal
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|d2
operator|=
name|newCal
argument_list|(
name|d1
argument_list|,
name|GMT
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|v1
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d1
argument_list|)
argument_list|)
expr_stmt|;
name|v2
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"v1< v2"
argument_list|,
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"v2> v1"
argument_list|,
name|v2
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|d1
operator|=
name|newCal
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|d2
operator|=
name|newCal
argument_list|(
name|d1
argument_list|,
name|GMT
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|v1
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d1
argument_list|)
argument_list|)
expr_stmt|;
name|v2
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"v1> v2"
argument_list|,
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"v2< v1"
argument_list|,
name|v2
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
comment|// same time zone. Non Zulu.
name|d1
operator|=
name|newCal
argument_list|(
literal|null
argument_list|,
name|PST
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|d2
operator|=
name|newCal
argument_list|(
name|d1
argument_list|,
name|PST
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|v1
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d1
argument_list|)
argument_list|)
expr_stmt|;
name|v2
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v1 and v2 should be equals"
argument_list|,
literal|0
argument_list|,
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v1 and v2 should be equals"
argument_list|,
literal|0
argument_list|,
name|v2
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|=
name|newCal
argument_list|(
literal|null
argument_list|,
name|PST
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|d2
operator|=
name|newCal
argument_list|(
name|d1
argument_list|,
name|PST
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|v1
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d1
argument_list|)
argument_list|)
expr_stmt|;
name|v2
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"v1< v2"
argument_list|,
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"v2> v1"
argument_list|,
name|v2
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|d1
operator|=
name|newCal
argument_list|(
literal|null
argument_list|,
name|PST
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|d2
operator|=
name|newCal
argument_list|(
name|d1
argument_list|,
name|PST
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|v1
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d1
argument_list|)
argument_list|)
expr_stmt|;
name|v2
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"v1> v2"
argument_list|,
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"v2< v1"
argument_list|,
name|v2
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
comment|// ahead time zone
name|d1
operator|=
name|newCal
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|d2
operator|=
name|newCal
argument_list|(
name|d1
argument_list|,
name|CET
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|v1
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d1
argument_list|)
argument_list|)
expr_stmt|;
name|v2
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v1 and v2 should be equals"
argument_list|,
literal|0
argument_list|,
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v1 and v2 should be equals"
argument_list|,
literal|0
argument_list|,
name|v2
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|=
name|newCal
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|d2
operator|=
name|newCal
argument_list|(
name|d1
argument_list|,
name|CET
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|v1
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d1
argument_list|)
argument_list|)
expr_stmt|;
name|v2
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"v1< v2"
argument_list|,
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"v2> v1"
argument_list|,
name|v2
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|d1
operator|=
name|newCal
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|d2
operator|=
name|newCal
argument_list|(
name|d1
argument_list|,
name|CET
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|v1
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d1
argument_list|)
argument_list|)
expr_stmt|;
name|v2
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"v1> v2"
argument_list|,
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"v2< v1"
argument_list|,
name|v2
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
comment|// behind time zone
name|d1
operator|=
name|newCal
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|d2
operator|=
name|newCal
argument_list|(
name|d1
argument_list|,
name|PST
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|v1
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d1
argument_list|)
argument_list|)
expr_stmt|;
name|v2
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v1 and v2 should be equals"
argument_list|,
literal|0
argument_list|,
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v1 and v2 should be equals"
argument_list|,
literal|0
argument_list|,
name|v2
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|=
name|newCal
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|d2
operator|=
name|newCal
argument_list|(
name|d1
argument_list|,
name|PST
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|v1
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d1
argument_list|)
argument_list|)
expr_stmt|;
name|v2
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"v1< v2"
argument_list|,
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"v2> v1"
argument_list|,
name|v2
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|d1
operator|=
name|newCal
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|d2
operator|=
name|newCal
argument_list|(
name|d1
argument_list|,
name|PST
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|v1
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d1
argument_list|)
argument_list|)
expr_stmt|;
name|v2
operator|=
name|newDate
argument_list|(
name|format
argument_list|(
name|d2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"v1> v2"
argument_list|,
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"v2< v1"
argument_list|,
name|v2
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**      * same as {@link #newCal(Calendar, TimeZone, int)} passing {@code null} as {@code TimeZone} and      * {@code 0} as {@code int}      *       * @param start      * @return      */
specifier|private
specifier|static
name|Calendar
name|newCal
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|Calendar
name|start
parameter_list|)
block|{
return|return
name|newCal
argument_list|(
name|start
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**      * return a new Calendar instance with the same time as the one provided as input.      *       * @param start the calendar we want the new one have the same time of. If null it will be a new      *            calendar instance      * @param the desired time zone. If null, GMT will be used.      * @param hoursOffset how many hours should we move the clock.      * @return      */
specifier|private
specifier|static
name|Calendar
name|newCal
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|Calendar
name|start
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|TimeZone
name|tz
parameter_list|,
specifier|final
name|int
name|hoursOffset
parameter_list|)
block|{
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
name|setTimeZone
argument_list|(
name|tz
operator|==
literal|null
condition|?
name|GMT
else|:
name|tz
argument_list|)
expr_stmt|;
if|if
condition|(
name|start
operator|==
literal|null
condition|)
block|{
return|return
name|c
return|;
block|}
else|else
block|{
name|c
operator|.
name|setTime
argument_list|(
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|add
argument_list|(
name|HOUR_OF_DAY
argument_list|,
name|hoursOffset
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
block|}
block|}
end_class

end_unit

