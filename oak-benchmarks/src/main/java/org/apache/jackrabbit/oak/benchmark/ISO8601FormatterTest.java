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
name|benchmark
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

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
name|time
operator|.
name|Instant
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|ZoneId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|format
operator|.
name|DateTimeFormatter
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
name|Date
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
name|util
operator|.
name|ISO8601
import|;
end_import

begin_class
specifier|public
class|class
name|ISO8601FormatterTest
extends|extends
name|AbstractTest
argument_list|<
name|Object
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TYPE
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"ISO8601FormatterTest"
argument_list|,
literal|"jcr-commons-cal"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|int
name|COUNT
init|=
literal|1000000
decl_stmt|;
specifier|private
specifier|static
name|TimeZone
name|TZ
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"UTC"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|DateFormat
name|DTF7
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss.SSSZ"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|DateTimeFormatter
name|DTF8
init|=
name|DateTimeFormatter
operator|.
name|ofPattern
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss.SSSX"
argument_list|)
operator|.
name|withZone
argument_list|(
name|ZoneId
operator|.
name|of
argument_list|(
literal|"UTC"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|chars
init|=
literal|0
decl_stmt|;
if|if
condition|(
literal|"jcr-commons-cal"
operator|.
name|equals
argument_list|(
name|TYPE
argument_list|)
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Calendar
name|c
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|TZ
argument_list|)
decl_stmt|;
name|c
operator|.
name|setTimeInMillis
argument_list|(
operator|(
operator|(
name|long
operator|)
name|i
operator|)
operator|<<
literal|8
argument_list|)
expr_stmt|;
name|chars
operator|+=
name|ISO8601
operator|.
name|format
argument_list|(
name|c
argument_list|)
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
comment|// in jackrabbit trunk...
comment|//        } else if ("jcr-commons-long".equals(TYPE)) {
comment|//            for (int i = 0; i< COUNT; i++) {
comment|//                chars += ISO8601.format(((long) i)<< 8).length();
comment|//            }
block|}
elseif|else
if|if
condition|(
literal|"jdk-7-formatter-st"
operator|.
name|equals
argument_list|(
name|TYPE
argument_list|)
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
synchronized|synchronized
init|(
name|DTF7
init|)
block|{
name|chars
operator|+=
name|DTF7
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
operator|(
operator|(
name|long
operator|)
name|i
operator|)
operator|<<
literal|8
argument_list|)
argument_list|)
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
literal|"jdk-7-formatter-mt"
operator|.
name|equals
argument_list|(
name|TYPE
argument_list|)
condition|)
block|{
name|ThreadLocal
argument_list|<
name|DateFormat
argument_list|>
name|formatter
init|=
operator|new
name|ThreadLocal
argument_list|<
name|DateFormat
argument_list|>
argument_list|()
decl_stmt|;
name|formatter
operator|.
name|set
argument_list|(
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss.SSSZ"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
name|chars
operator|+=
name|formatter
operator|.
name|get
argument_list|()
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
operator|(
operator|(
name|long
operator|)
name|i
operator|)
operator|<<
literal|8
argument_list|)
argument_list|)
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"jdk-8-formatter"
operator|.
name|equals
argument_list|(
name|TYPE
argument_list|)
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
name|chars
operator|+=
name|DTF8
operator|.
name|format
argument_list|(
name|Instant
operator|.
name|ofEpochMilli
argument_list|(
operator|(
operator|(
name|long
operator|)
name|i
operator|)
operator|<<
literal|8
argument_list|)
argument_list|)
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|chars
operator|!=
literal|24
operator|*
name|COUNT
operator|&&
name|chars
operator|!=
literal|28
operator|*
name|COUNT
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"formatted strings did not have the expected length: "
operator|+
name|chars
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

