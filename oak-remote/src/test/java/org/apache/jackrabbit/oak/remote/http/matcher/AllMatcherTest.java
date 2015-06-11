begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|remote
operator|.
name|http
operator|.
name|matcher
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
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
name|assertFalse
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
name|mockito
operator|.
name|Mockito
operator|.
name|doReturn
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_class
specifier|public
class|class
name|AllMatcherTest
block|{
name|boolean
name|matches
parameter_list|(
name|boolean
modifier|...
name|results
parameter_list|)
block|{
name|Matcher
index|[]
name|matchers
init|=
operator|new
name|Matcher
index|[
name|results
operator|.
name|length
index|]
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
name|results
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|matchers
index|[
name|i
index|]
operator|=
name|mock
argument_list|(
name|Matcher
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|HttpServletRequest
name|request
init|=
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
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
name|results
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|doReturn
argument_list|(
name|results
index|[
name|i
index|]
argument_list|)
operator|.
name|when
argument_list|(
name|matchers
index|[
name|i
index|]
argument_list|)
operator|.
name|match
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|AllMatcher
argument_list|(
name|matchers
argument_list|)
operator|.
name|match
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNoMatchers
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|matches
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSingleMatcher
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|matches
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matches
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchers
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|matches
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matches
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matches
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matches
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

