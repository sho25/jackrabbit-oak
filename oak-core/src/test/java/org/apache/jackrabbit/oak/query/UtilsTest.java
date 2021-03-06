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
name|query
package|;
end_package

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
name|UtilsTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|saturatedAdd
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|QueryImpl
operator|.
name|saturatedAdd
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|QueryImpl
operator|.
name|saturatedAdd
argument_list|(
literal|1
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|QueryImpl
operator|.
name|saturatedAdd
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|QueryImpl
operator|.
name|saturatedAdd
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|long
index|[]
name|test
init|=
block|{
name|Long
operator|.
name|MIN_VALUE
block|,
name|Long
operator|.
name|MIN_VALUE
operator|+
literal|1
block|,
name|Long
operator|.
name|MIN_VALUE
operator|+
literal|10
block|,
operator|-
literal|1000
block|,
operator|-
literal|10
block|,
operator|-
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|3
block|,
literal|10000
block|,
name|Long
operator|.
name|MAX_VALUE
operator|-
literal|20
block|,
name|Long
operator|.
name|MAX_VALUE
operator|-
literal|1
block|,
name|Long
operator|.
name|MAX_VALUE
block|}
decl_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|long
name|x
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
condition|?
name|test
index|[
name|r
operator|.
name|nextInt
argument_list|(
name|test
operator|.
name|length
argument_list|)
index|]
else|:
name|r
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|long
name|y
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
condition|?
name|test
index|[
name|r
operator|.
name|nextInt
argument_list|(
name|test
operator|.
name|length
argument_list|)
index|]
else|:
name|r
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|long
name|alt
init|=
name|altSaturatedAdd
argument_list|(
name|x
argument_list|,
name|y
argument_list|)
decl_stmt|;
name|long
name|got
init|=
name|QueryImpl
operator|.
name|saturatedAdd
argument_list|(
name|x
argument_list|,
name|y
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|x
operator|+
literal|"+"
operator|+
name|y
argument_list|,
name|alt
argument_list|,
name|got
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|long
name|altSaturatedAdd
parameter_list|(
name|long
name|x
parameter_list|,
name|long
name|y
parameter_list|)
block|{
comment|// see also http://stackoverflow.com/questions/2632392/saturated-addition-of-two-signed-java-long-values
if|if
condition|(
name|x
operator|>
literal|0
operator|!=
name|y
operator|>
literal|0
condition|)
block|{
comment|// different signs
return|return
name|x
operator|+
name|y
return|;
block|}
elseif|else
if|if
condition|(
name|x
operator|>
literal|0
condition|)
block|{
comment|// can overflow
return|return
name|Long
operator|.
name|MAX_VALUE
operator|-
name|x
operator|<
name|y
condition|?
name|Long
operator|.
name|MAX_VALUE
else|:
name|x
operator|+
name|y
return|;
block|}
else|else
block|{
comment|// can underflow
return|return
name|Long
operator|.
name|MIN_VALUE
operator|-
name|x
operator|>
name|y
condition|?
name|Long
operator|.
name|MIN_VALUE
else|:
name|x
operator|+
name|y
return|;
block|}
block|}
block|}
end_class

end_unit

