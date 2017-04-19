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
name|commons
operator|.
name|jmx
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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

begin_class
specifier|public
class|class
name|JmxUtilTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|quotation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"text"
argument_list|,
name|JmxUtil
operator|.
name|quoteValueIfRequired
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|TestCase
operator|.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|JmxUtil
operator|.
name|quoteValueIfRequired
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|JmxUtil
operator|.
name|quoteValueIfRequired
argument_list|(
literal|"text*with?chars"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|quoteAndComma
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|JmxUtil
operator|.
name|quoteValueIfRequired
argument_list|(
literal|"text,withComma"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|JmxUtil
operator|.
name|quoteValueIfRequired
argument_list|(
literal|"text=withEqual"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
