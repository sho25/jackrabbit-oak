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
name|plugins
operator|.
name|version
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|RepositoryException
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
name|state
operator|.
name|NodeBuilder
import|;
end_import

begin_comment
comment|/**  *<i>Inspired by Jackrabbit 2.x</i>  *<p>  * This Interface defines the version selector that needs to provide a version,  * given some hints and a version history. the selector is used in the various  * restore methods in order to select the correct version of previously versioned  * OPV=Version children upon restore. JSR170 states:<em>"This determination  * [of the version] depends on the configuration of the workspace and is outside  * the scope of this specification."</em>  *<p>  * The version selection in jackrabbit works as follows:<br/>  * The {@code Node.restore()} methods uses the  * {@link DateVersionSelector} which is initialized with the creation date of  * the parent version. This selector selects the latest version that is equal  * or older than the given date. if no such version exists, the initial one  * is restored.<br/>  *<p>  *  * @see DateVersionSelector  * @see javax.jcr.version.VersionManager#restore  */
end_comment

begin_interface
specifier|public
interface|interface
name|VersionSelector
block|{
comment|/**      * Selects a version of the given version history. If this VersionSelector      * is unable to select one, it can return {@code null}. Please note,      * that a version selector is not allowed to return the root version.      *      * @param versionHistory version history to select a version from      * @return A version or {@code null}.      * @throws RepositoryException if an error occurs.      */
annotation|@
name|CheckForNull
name|NodeBuilder
name|select
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|versionHistory
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
block|}
end_interface

end_unit

