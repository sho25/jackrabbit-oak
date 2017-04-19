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
name|api
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|TypeComparisonTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|compareTypes
parameter_list|()
block|{
name|Type
index|[]
name|types
init|=
operator|new
name|Type
index|[]
block|{
name|Type
operator|.
name|BINARIES
block|,
name|Type
operator|.
name|BINARY
block|,
name|Type
operator|.
name|BOOLEANS
block|,
name|Type
operator|.
name|BOOLEAN
block|,
name|Type
operator|.
name|DATES
block|,
name|Type
operator|.
name|DATE
block|,
name|Type
operator|.
name|DECIMALS
block|,
name|Type
operator|.
name|DECIMAL
block|,
name|Type
operator|.
name|DOUBLES
block|,
name|Type
operator|.
name|DOUBLE
block|,
name|Type
operator|.
name|LONGS
block|,
name|Type
operator|.
name|LONG
block|,
name|Type
operator|.
name|NAMES
block|,
name|Type
operator|.
name|NAME
block|,
name|Type
operator|.
name|PATHS
block|,
name|Type
operator|.
name|PATH
block|,
name|Type
operator|.
name|REFERENCES
block|,
name|Type
operator|.
name|REFERENCE
block|,
name|Type
operator|.
name|STRINGS
block|,
name|Type
operator|.
name|STRING
block|,
name|Type
operator|.
name|UNDEFINEDS
block|,
name|Type
operator|.
name|UNDEFINED
block|,
name|Type
operator|.
name|URIS
block|,
name|Type
operator|.
name|URI
block|,
name|Type
operator|.
name|WEAKREFERENCES
block|,
name|Type
operator|.
name|WEAKREFERENCE
block|,          }
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|types
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
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|types
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|<
name|j
condition|)
block|{
name|assertTypeLessThan
argument_list|(
name|types
index|[
name|i
index|]
argument_list|,
name|types
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|j
operator|<
name|i
condition|)
block|{
name|assertTypeLessThan
argument_list|(
name|types
index|[
name|j
index|]
argument_list|,
name|types
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|==
name|j
condition|)
block|{
name|assertTypeEqual
argument_list|(
name|types
index|[
name|i
index|]
argument_list|,
name|types
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|assertTypeLessThan
parameter_list|(
name|Type
argument_list|<
name|?
argument_list|>
name|a
parameter_list|,
name|Type
argument_list|<
name|?
argument_list|>
name|b
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|a
operator|.
name|compareTo
argument_list|(
name|b
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|compareTo
argument_list|(
name|a
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|a
operator|.
name|tag
argument_list|()
operator|>
name|b
operator|.
name|tag
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Types should be ordered by increasing tag value"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|b
operator|.
name|tag
argument_list|()
operator|==
name|a
operator|.
name|tag
argument_list|()
operator|&&
name|a
operator|.
name|isArray
argument_list|()
operator|&&
operator|!
name|b
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"If their tag is the same, types should be ordered by multiplicity"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|assertTypeEqual
parameter_list|(
name|Type
argument_list|<
name|?
argument_list|>
name|a
parameter_list|,
name|Type
argument_list|<
name|?
argument_list|>
name|b
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|a
operator|.
name|compareTo
argument_list|(
name|b
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|tag
argument_list|()
operator|==
name|b
operator|.
name|tag
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|isArray
argument_list|()
operator|==
name|b
operator|.
name|isArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
