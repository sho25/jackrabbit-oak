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
name|jcr
operator|.
name|tck
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|test
operator|.
name|ConcurrentTestSuite
import|;
end_import

begin_class
specifier|public
class|class
name|VersionIT
extends|extends
name|ConcurrentTestSuite
block|{
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
operator|new
name|VersionIT
argument_list|()
return|;
block|}
specifier|public
name|VersionIT
parameter_list|()
block|{
name|super
argument_list|(
literal|"JCR version tests"
argument_list|)
expr_stmt|;
name|addTest
argument_list|(
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|test
operator|.
name|api
operator|.
name|version
operator|.
name|TestAll
operator|.
name|suite
argument_list|()
argument_list|)
expr_stmt|;
name|addTest
argument_list|(
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|test
operator|.
name|api
operator|.
name|version
operator|.
name|simple
operator|.
name|TestAll
operator|.
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

