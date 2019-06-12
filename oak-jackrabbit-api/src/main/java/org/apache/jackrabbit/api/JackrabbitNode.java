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
package|;
end_package

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
name|lock
operator|.
name|LockException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|ConstraintViolationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NoSuchNodeTypeException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|VersionException
import|;
end_import

begin_comment
comment|/**  * The Jackrabbit Node interface. This interface contains the  * Jackrabbit-specific extensions to the JCR {@link javax.jcr.Node} interface.  */
end_comment

begin_interface
specifier|public
interface|interface
name|JackrabbitNode
block|{
comment|/**      *       * @param newName      * @throws javax.jcr.RepositoryException      */
name|void
name|rename
parameter_list|(
name|String
name|newName
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      *      * @param mixinNames      * @throws NoSuchNodeTypeException      * @throws VersionException      * @throws ConstraintViolationException      * @throws LockException      * @throws RepositoryException      */
name|void
name|setMixins
parameter_list|(
name|String
index|[]
name|mixinNames
parameter_list|)
throws|throws
name|NoSuchNodeTypeException
throws|,
name|VersionException
throws|,
name|ConstraintViolationException
throws|,
name|LockException
throws|,
name|RepositoryException
function_decl|;
block|}
end_interface

end_unit

