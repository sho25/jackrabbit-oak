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
name|jcr
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

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
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
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
name|query
operator|.
name|QueryEngineSettings
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
name|spi
operator|.
name|security
operator|.
name|principal
operator|.
name|EveryonePrincipal
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
name|NotExecutableException
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
name|RepositoryStub
import|;
end_import

begin_comment
comment|/**  * Base RepositoryStub for the Oak Repository  */
end_comment

begin_class
specifier|abstract
class|class
name|OakRepositoryStub
extends|extends
name|RepositoryStub
block|{
specifier|private
specifier|static
specifier|final
name|Principal
name|UNKNOWN_PRINCIPAL
init|=
operator|new
name|Principal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"an_unknown_user"
return|;
block|}
block|}
decl_stmt|;
specifier|protected
name|OakRepositoryStub
parameter_list|(
name|Properties
name|env
parameter_list|)
block|{
name|super
argument_list|(
name|env
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|QueryEngineSettings
name|getQueryEngineSettings
parameter_list|()
block|{
name|QueryEngineSettings
name|settings
init|=
operator|new
name|QueryEngineSettings
argument_list|()
decl_stmt|;
name|settings
operator|.
name|setFullTextComparisonWithoutIndex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|settings
return|;
block|}
annotation|@
name|Override
specifier|public
name|Credentials
name|getReadOnlyCredentials
parameter_list|()
block|{
return|return
operator|new
name|GuestCredentials
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Principal
name|getKnownPrincipal
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Principal
name|getUnknownPrincipal
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
return|return
name|UNKNOWN_PRINCIPAL
return|;
block|}
block|}
end_class

end_unit

