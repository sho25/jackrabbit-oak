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
name|api
operator|.
name|jmx
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_comment
comment|/**  * Interface for managing a JCR repository as a JMX MBean.  *  * @since Apache Jackrabbit 2.3  */
end_comment

begin_interface
specifier|public
interface|interface
name|ManagedRepositoryMBean
block|{
comment|/**      * Returns the name of this repository implementation.      *      * @see javax.jcr.Repository#REP_NAME_DESC      * @return name of this repository implementation      */
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Returns the version of this repository implementation.      *      * @see javax.jcr.Repository#REP_VERSION_DESC      * @return version of this repository implementation      */
name|String
name|getVersion
parameter_list|()
function_decl|;
comment|/**      * Returns all the repository descriptors.      *      * @return repository descriptors      */
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getDescriptors
parameter_list|()
function_decl|;
comment|/**      * Returns the names of all the workspaces in this repository.      *      * @return workspace names      */
name|String
index|[]
name|getWorkspaceNames
parameter_list|()
function_decl|;
comment|/**      * Creates a new workspace with the given name.      *      * @param name workspace name      * @throws RepositoryException if the workspace could not be created      */
name|void
name|createWorkspace
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
block|}
end_interface

end_unit

