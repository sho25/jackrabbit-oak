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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
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
name|Oak
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
name|fixture
operator|.
name|JcrCreator
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
name|fixture
operator|.
name|OakRepositoryFixture
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
name|fixture
operator|.
name|RepositoryFixture
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
name|jcr
operator|.
name|Jcr
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
name|security
operator|.
name|SecurityProviderImpl
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
name|security
operator|.
name|authorization
operator|.
name|composite
operator|.
name|CompositeAuthorizationConfiguration
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
name|ConfigurationParameters
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
name|SecurityProvider
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
name|authorization
operator|.
name|AuthorizationConfiguration
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
name|authorization
operator|.
name|cug
operator|.
name|impl
operator|.
name|CugConfiguration
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * Test the effect of multiple authorization configurations on the general read  * operations.  *  * TODO: setup configured number of cugs.  */
end_comment

begin_class
specifier|public
class|class
name|CugTest
extends|extends
name|ReadDeepTreeTest
block|{
specifier|private
specifier|final
name|ConfigurationParameters
name|params
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|reverseOrder
decl_stmt|;
specifier|protected
name|CugTest
parameter_list|(
name|boolean
name|runAsAdmin
parameter_list|,
name|int
name|itemsToRead
parameter_list|,
name|boolean
name|singleSession
parameter_list|,
annotation|@
name|Nonnull
name|List
argument_list|<
name|String
argument_list|>
name|supportedPaths
parameter_list|,
name|boolean
name|reverseOrder
parameter_list|)
block|{
name|super
argument_list|(
name|runAsAdmin
argument_list|,
name|itemsToRead
argument_list|,
literal|false
argument_list|,
name|singleSession
argument_list|)
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|AuthorizationConfiguration
operator|.
name|NAME
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
literal|"cugSupportedPaths"
argument_list|,
name|supportedPaths
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|supportedPaths
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
literal|"cugEnabled"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|reverseOrder
operator|=
name|reverseOrder
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Repository
index|[]
name|createRepository
parameter_list|(
name|RepositoryFixture
name|fixture
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|fixture
operator|instanceof
name|OakRepositoryFixture
condition|)
block|{
return|return
operator|(
operator|(
name|OakRepositoryFixture
operator|)
name|fixture
operator|)
operator|.
name|setUpCluster
argument_list|(
literal|1
argument_list|,
operator|new
name|JcrCreator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Jcr
name|customize
parameter_list|(
name|Oak
name|oak
parameter_list|)
block|{
return|return
operator|new
name|Jcr
argument_list|(
name|oak
argument_list|)
operator|.
name|with
argument_list|(
name|createSecurityProvider
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Fixture "
operator|+
name|fixture
operator|+
literal|" not supported for this benchmark."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getImportFileName
parameter_list|()
block|{
return|return
literal|"deepTree_everyone.xml"
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getTestNodeName
parameter_list|()
block|{
return|return
literal|"CugTest"
return|;
block|}
specifier|protected
name|SecurityProvider
name|createSecurityProvider
parameter_list|()
block|{
return|return
operator|new
name|TmpSecurityProvider
argument_list|(
name|params
argument_list|,
name|reverseOrder
argument_list|)
return|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|TmpSecurityProvider
extends|extends
name|SecurityProviderImpl
block|{
specifier|private
name|TmpSecurityProvider
parameter_list|(
annotation|@
name|Nonnull
name|ConfigurationParameters
name|params
parameter_list|,
name|boolean
name|reverseOrder
parameter_list|)
block|{
name|super
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|AuthorizationConfiguration
name|authorizationConfiguration
init|=
name|getConfiguration
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|AuthorizationConfiguration
name|defaultAuthorization
init|=
name|checkNotNull
argument_list|(
operator|(
operator|(
name|CompositeAuthorizationConfiguration
operator|)
name|authorizationConfiguration
operator|)
operator|.
name|getDefaultConfig
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|reverseOrder
condition|)
block|{
name|bindAuthorizationConfiguration
argument_list|(
name|defaultAuthorization
argument_list|)
expr_stmt|;
name|bindAuthorizationConfiguration
argument_list|(
operator|new
name|CugConfiguration
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bindAuthorizationConfiguration
argument_list|(
operator|new
name|CugConfiguration
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|bindAuthorizationConfiguration
argument_list|(
name|defaultAuthorization
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
