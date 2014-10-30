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
name|RepositoryException
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
name|PropertyDefinition
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
name|Tree
import|;
end_import

begin_comment
comment|/**  * {@code ProtectedPropertyImporter} is in charge of importing single  * properties with a protected {@code PropertyDefinition}.  *  * @see ProtectedNodeImporter for an abstract class used to import protected  * nodes and the subtree below them.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ProtectedPropertyImporter
extends|extends
name|ProtectedItemImporter
block|{
comment|/**      * Handles a single protected property.      *      * @param parent The affected parent node.      * @param protectedPropInfo The {@code PropInfo} to be imported.      * @param def The property definition determined by the importer that      * calls this method.      * @return {@code true} If the property could be successfully imported;      * {@code false} otherwise.      * @throws javax.jcr.RepositoryException If an error occurs.      */
name|boolean
name|handlePropInfo
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|parent
parameter_list|,
annotation|@
name|Nonnull
name|PropInfo
name|protectedPropInfo
parameter_list|,
annotation|@
name|Nonnull
name|PropertyDefinition
name|def
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Informs this importer that all properties to be imported below      * {@code protectedParent} have been processed by the importer. If this importer      * did not import any protected properties this method doesn't do anything.      * Otherwise it may perform some validation and cleanup required based      * on the set of protected properties handled by this importer.      *      * @param protectedParent The protected parent tree.      * @throws IllegalStateException If this method is called in an illegal state.      * @throws javax.jcr.nodetype.ConstraintViolationException If the set of      * properties was incomplete and the importer was not able to fix the problem.      * @throws RepositoryException If another error occurs.      */
name|void
name|propertiesCompleted
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|protectedParent
parameter_list|)
throws|throws
name|IllegalStateException
throws|,
name|ConstraintViolationException
throws|,
name|RepositoryException
function_decl|;
block|}
end_interface

end_unit

