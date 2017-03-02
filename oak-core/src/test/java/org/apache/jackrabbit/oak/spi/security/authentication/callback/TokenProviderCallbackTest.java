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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|callback
package|;
end_package

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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|token
operator|.
name|TokenProvider
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
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|assertSame
import|;
end_import

begin_class
specifier|public
class|class
name|TokenProviderCallbackTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testCallback
parameter_list|()
block|{
name|TokenProviderCallback
name|cb
init|=
operator|new
name|TokenProviderCallback
argument_list|()
decl_stmt|;
name|TokenProvider
name|tp
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|TokenProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|cb
operator|.
name|setTokenProvider
argument_list|(
name|tp
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|tp
argument_list|,
name|cb
operator|.
name|getTokenProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

