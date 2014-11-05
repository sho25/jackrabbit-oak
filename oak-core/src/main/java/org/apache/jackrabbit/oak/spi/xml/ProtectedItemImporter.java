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
name|api
operator|.
name|Root
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
name|namepath
operator|.
name|NamePathMapper
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

begin_comment
comment|/**  * Base interface for {@link ProtectedNodeImporter} and {@link ProtectedPropertyImporter}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ProtectedItemImporter
block|{
comment|/**      * Parameter name for the import behavior configuration option.      */
name|String
name|PARAM_IMPORT_BEHAVIOR
init|=
literal|"importBehavior"
decl_stmt|;
comment|/**      * Initializes the importer.      *      * @param session The session that is running the import.      * @param root The root associated with the import.      * @param namePathMapper The name/path mapper used to translate names      * between their jcr and oak form.      * @param isWorkspaceImport A flag indicating whether the import has been      * started from the {@link javax.jcr.Workspace} or from the      * {@link javax.jcr.Session}. Implementations are free to implement both      * types of imports or only a single one. For example it doesn't make sense      * to allow for importing versions along with a Session import as      * version operations are required to never leave transient changes behind.      * @param uuidBehavior The uuid behavior specified with the import call.      * @param referenceTracker The uuid/reference helper.      * @param securityProvider The security provider.      * @return {@code true} if this importer was successfully initialized and      * is able to handle an import with the given setup; {@code false} otherwise.      */
name|boolean
name|init
parameter_list|(
annotation|@
name|Nonnull
name|Session
name|session
parameter_list|,
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|NamePathMapper
name|namePathMapper
parameter_list|,
name|boolean
name|isWorkspaceImport
parameter_list|,
name|int
name|uuidBehavior
parameter_list|,
annotation|@
name|Nonnull
name|ReferenceChangeTracker
name|referenceTracker
parameter_list|,
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|)
function_decl|;
comment|/**      * Post processing protected reference properties underneath a protected      * or non-protected parent node. If the parent is protected it has been      * handled by this importer already.      *      * @throws javax.jcr.RepositoryException If an error occurs.      */
name|void
name|processReferences
parameter_list|()
throws|throws
name|RepositoryException
function_decl|;
block|}
end_interface

end_unit

