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
name|xml
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
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_comment
comment|/**  * Content importer. The XML import handlers use this interface to submit  * the parsed content to the repository. The implementation of this class  * decides how the content is actually persisted; either through the  * transient space of a session, or directly into the workspace.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Importer
block|{
comment|/**      * Called once at the beginning of the content import.      *      * @throws RepositoryException on a repository error      */
name|void
name|start
parameter_list|()
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Called to start the import of a node. Information about the      * imported node and all it's properties are passed as arguments.      * Possible child nodes are imported recursively using this same      * method until a {@link #endNode(NodeInfo)} call is made with the      * same node information.      *      * @param nodeInfo  information about the node being imported      * @param propInfos information about the properties being imported      *                  (list of {@link PropInfo} instances)      * @throws RepositoryException on a repository error      */
name|void
name|startNode
parameter_list|(
name|NodeInfo
name|nodeInfo
parameter_list|,
name|List
argument_list|<
name|PropInfo
argument_list|>
name|propInfos
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Called to end the import of a node. This method is called after      * a {@link #startNode(NodeInfo, List)} call with the same node      * information and after all the possible child nodes have been      * imported with respective startNode/endNode calls.      *<p>      * Just like XML elements, the startNode/endNode calls are guaranteed      * to be properly nested and complete.      *      * @param nodeInfo information about the node being imported      * @throws RepositoryException on a repository error      */
name|void
name|endNode
parameter_list|(
name|NodeInfo
name|nodeInfo
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Called once at the end of the content import.      *      * @throws RepositoryException on a repository error      */
name|void
name|end
parameter_list|()
throws|throws
name|RepositoryException
function_decl|;
block|}
end_interface

end_unit

