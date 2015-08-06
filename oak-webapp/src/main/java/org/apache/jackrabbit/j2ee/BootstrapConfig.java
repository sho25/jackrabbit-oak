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
name|j2ee
package|;
end_package

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
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletConfig
import|;
end_import

begin_comment
comment|/**  * The bootstrap configuration hold information about initial startup  * parameters like repository config and home.  *  * It supports the following properties and init parameters:  *<xmp>  * +-------------------+-------------------+  * | Property Name     | Init-Param Name   |  * +-------------------+-------------------+  * | repository.home   | repository-home   |  * | repository.config | repository-config |  * | repository.name   | repository-name   |  * +-------------------+-------------------+  *</xmp>  */
end_comment

begin_class
specifier|public
class|class
name|BootstrapConfig
extends|extends
name|AbstractConfig
block|{
specifier|private
name|String
name|repositoryHome
decl_stmt|;
specifier|private
name|String
name|repositoryConfig
decl_stmt|;
specifier|private
name|String
name|repositoryName
decl_stmt|;
specifier|private
name|JNDIConfig
name|jndiConfig
init|=
operator|new
name|JNDIConfig
argument_list|(
name|this
argument_list|)
decl_stmt|;
specifier|private
name|RMIConfig
name|rmiConfig
init|=
operator|new
name|RMIConfig
argument_list|(
name|this
argument_list|)
decl_stmt|;
specifier|public
name|void
name|init
parameter_list|(
name|Properties
name|props
parameter_list|)
throws|throws
name|ServletException
block|{
name|super
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|jndiConfig
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|rmiConfig
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|ServletConfig
name|ctx
parameter_list|)
throws|throws
name|ServletException
block|{
name|super
operator|.
name|init
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|jndiConfig
operator|.
name|init
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|rmiConfig
operator|.
name|init
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getRepositoryHome
parameter_list|()
block|{
return|return
name|repositoryHome
return|;
block|}
specifier|public
name|void
name|setRepositoryHome
parameter_list|(
name|String
name|repositoryHome
parameter_list|)
block|{
name|this
operator|.
name|repositoryHome
operator|=
name|repositoryHome
expr_stmt|;
block|}
specifier|public
name|String
name|getRepositoryConfig
parameter_list|()
block|{
return|return
name|repositoryConfig
return|;
block|}
specifier|public
name|void
name|setRepositoryConfig
parameter_list|(
name|String
name|repositoryConfig
parameter_list|)
block|{
name|this
operator|.
name|repositoryConfig
operator|=
name|repositoryConfig
expr_stmt|;
block|}
specifier|public
name|String
name|getRepositoryName
parameter_list|()
block|{
return|return
name|repositoryName
return|;
block|}
specifier|public
name|void
name|setRepositoryName
parameter_list|(
name|String
name|repositoryName
parameter_list|)
block|{
name|this
operator|.
name|repositoryName
operator|=
name|repositoryName
expr_stmt|;
block|}
specifier|public
name|JNDIConfig
name|getJndiConfig
parameter_list|()
block|{
return|return
name|jndiConfig
return|;
block|}
specifier|public
name|RMIConfig
name|getRmiConfig
parameter_list|()
block|{
return|return
name|rmiConfig
return|;
block|}
specifier|public
name|void
name|validate
parameter_list|()
block|{
name|valid
operator|=
name|repositoryName
operator|!=
literal|null
expr_stmt|;
name|jndiConfig
operator|.
name|validate
argument_list|()
expr_stmt|;
name|rmiConfig
operator|.
name|validate
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|logInfos
parameter_list|()
block|{
name|super
operator|.
name|logInfos
argument_list|()
expr_stmt|;
if|if
condition|(
name|jndiConfig
operator|.
name|isValid
argument_list|()
condition|)
block|{
name|jndiConfig
operator|.
name|logInfos
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|rmiConfig
operator|.
name|isValid
argument_list|()
condition|)
block|{
name|rmiConfig
operator|.
name|logInfos
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

