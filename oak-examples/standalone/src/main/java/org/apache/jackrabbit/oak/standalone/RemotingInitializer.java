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
name|standalone
package|;
end_package

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
name|server
operator|.
name|remoting
operator|.
name|davex
operator|.
name|DavexServletService
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
name|server
operator|.
name|remoting
operator|.
name|davex
operator|.
name|JcrRemotingServlet
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
name|webdav
operator|.
name|simple
operator|.
name|SimpleWebdavServlet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|beans
operator|.
name|factory
operator|.
name|annotation
operator|.
name|Autowired
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|beans
operator|.
name|factory
operator|.
name|annotation
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|boot
operator|.
name|context
operator|.
name|embedded
operator|.
name|ServletRegistrationBean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|annotation
operator|.
name|Bean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|annotation
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * Configures the Webdav and Davex servlet to enabled remote  * access to the repository  */
end_comment

begin_class
annotation|@
name|Configuration
specifier|public
class|class
name|RemotingInitializer
block|{
annotation|@
name|Value
argument_list|(
literal|"${repo.home}/dav"
argument_list|)
specifier|private
name|String
name|davHome
decl_stmt|;
annotation|@
name|Autowired
specifier|private
name|Repository
name|repository
decl_stmt|;
annotation|@
name|Bean
specifier|public
name|ServletRegistrationBean
name|webDavServlet
parameter_list|()
block|{
name|ServletRegistrationBean
name|bean
init|=
operator|new
name|ServletRegistrationBean
argument_list|(
operator|new
name|SimpleWebdavServlet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Repository
name|getRepository
parameter_list|()
block|{
return|return
name|repository
return|;
block|}
block|}
argument_list|,
literal|"/repository/*"
argument_list|)
decl_stmt|;
name|bean
operator|.
name|addInitParameter
argument_list|(
name|SimpleWebdavServlet
operator|.
name|INIT_PARAM_RESOURCE_PATH_PREFIX
argument_list|,
literal|"/repository"
argument_list|)
expr_stmt|;
name|bean
operator|.
name|addInitParameter
argument_list|(
name|SimpleWebdavServlet
operator|.
name|INIT_PARAM_RESOURCE_CONFIG
argument_list|,
literal|"webdav-config.xml"
argument_list|)
expr_stmt|;
return|return
name|bean
return|;
block|}
annotation|@
name|Bean
specifier|public
name|ServletRegistrationBean
name|remotingServlet
parameter_list|()
block|{
name|ServletRegistrationBean
name|bean
init|=
operator|new
name|ServletRegistrationBean
argument_list|(
operator|new
name|DavexServletService
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Repository
name|getRepository
parameter_list|()
block|{
return|return
name|repository
return|;
block|}
block|}
argument_list|,
literal|"/server/*"
argument_list|)
decl_stmt|;
name|bean
operator|.
name|addInitParameter
argument_list|(
name|JcrRemotingServlet
operator|.
name|INIT_PARAM_RESOURCE_PATH_PREFIX
argument_list|,
literal|"/server"
argument_list|)
expr_stmt|;
name|bean
operator|.
name|addInitParameter
argument_list|(
name|JcrRemotingServlet
operator|.
name|INIT_PARAM_BATCHREAD_CONFIG
argument_list|,
literal|"batchread.properties"
argument_list|)
expr_stmt|;
comment|//TODO By docs this is meant to point to a file which gets loaded
comment|//but servlet always reads it as File not via input stream. Hence using
comment|//actual class
name|bean
operator|.
name|addInitParameter
argument_list|(
name|JcrRemotingServlet
operator|.
name|INIT_PARAM_PROTECTED_HANDLERS_CONFIG
argument_list|,
literal|"org.apache.jackrabbit.server.remoting.davex.AclRemoveHandler"
argument_list|)
expr_stmt|;
name|bean
operator|.
name|addInitParameter
argument_list|(
name|JcrRemotingServlet
operator|.
name|INIT_PARAM_HOME
argument_list|,
name|davHome
argument_list|)
expr_stmt|;
return|return
name|bean
return|;
block|}
block|}
end_class

end_unit

