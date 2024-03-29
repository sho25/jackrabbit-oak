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
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Credentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|GuestCredentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
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
name|AuthInfo
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
name|assertSame
import|;
end_import

begin_class
specifier|public
class|class
name|ImpersonationCredentialsTest
block|{
specifier|private
specifier|final
name|AuthInfo
name|info
init|=
operator|new
name|AuthInfoImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testGetBaseCredentials
parameter_list|()
block|{
name|Credentials
name|creds
init|=
operator|new
name|GuestCredentials
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|creds
argument_list|,
operator|new
name|ImpersonationCredentials
argument_list|(
name|creds
argument_list|,
name|info
argument_list|)
operator|.
name|getBaseCredentials
argument_list|()
argument_list|)
expr_stmt|;
name|Credentials
name|simpleCreds
init|=
operator|new
name|SimpleCredentials
argument_list|(
literal|"userId"
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|simpleCreds
argument_list|,
operator|new
name|ImpersonationCredentials
argument_list|(
name|simpleCreds
argument_list|,
name|info
argument_list|)
operator|.
name|getBaseCredentials
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAuthInfo
parameter_list|()
block|{
name|assertSame
argument_list|(
name|info
argument_list|,
operator|new
name|ImpersonationCredentials
argument_list|(
operator|new
name|Credentials
argument_list|()
block|{}
argument_list|,
name|info
argument_list|)
operator|.
name|getImpersonatorInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

