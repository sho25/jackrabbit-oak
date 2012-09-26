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
name|api
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

begin_comment
comment|/**  * The root of a {@link Tree}.  *<p>  * The data returned by this class filtered for the access rights that are set  * in the {@link ContentSession} that created this object.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Root
block|{
comment|/**      * Move the child located at {@code sourcePath} to a child at {@code destPath}.      * Both paths must be absolute and resolve to a child located beneath this      * root.<br>      *      * This method does nothing and returns {@code false} if      *<ul>      *<li>the tree at {@code sourcePath} does not exist or is not accessible,</li>      *<li>the parent of the tree at {@code destinationPath} does not exist or is not accessible,</li>      *<li>a tree already exists at {@code destinationPath}.</li>      *</ul>      * If a tree at {@code destinationPath} exists but is not accessible to the      * editing content session this method succeeds but a subsequent      * {@link #commit()} will detect the violation and fail.      *      * @param sourcePath The source path      * @param destPath The destination path      * @return {@code true} on success, {@code false} otherwise.      */
name|boolean
name|move
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destPath
parameter_list|)
function_decl|;
comment|/**      * Copy the child located at {@code sourcePath} to a child at {@code destPath}.      * Both paths must be absolute and resolve to a child located in this root.<br>      *      * This method does nothing an returns {@code false} if      *<ul>      *<li>The tree at {@code sourcePath} does exist or is not accessible,</li>      *<li>the parent of the tree at {@code destinationPath} does not exist or is not accessible,</li>      *<li>a tree already exists at {@code destinationPath}.</li>      *</ul>      * If a tree at {@code destinationPath} exists but is not accessible to the      * editing content session this method succeeds but a subsequent      * {@link #commit()} will detect the violation and fail.      *      * @param sourcePath source path      * @param destPath destination path      * @return  {@code true} on success, {@code false} otherwise.      */
name|boolean
name|copy
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destPath
parameter_list|)
function_decl|;
comment|/**      * Retrieve the {@code Tree} at the given absolute {@code path}. The path      * must resolve to a tree in this root.      *      * @param path absolute path to the tree      * @return tree at the given path or {@code null} if no such tree exists or      *         if the tree at {@code path} is not accessible.      */
annotation|@
name|CheckForNull
name|Tree
name|getTree
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
comment|/**      * Get a tree location for a given absolute {@code path}      *      * @param path absolute path to the location      * @return the tree location for {@code path}      */
annotation|@
name|Nonnull
name|TreeLocation
name|getLocation
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
comment|/**      * Rebase this root instance to the latest revision. After a call to this method,      * all trees obtained through {@link #getTree(String)} become invalid and fresh      * instances must be obtained.      */
name|void
name|rebase
parameter_list|()
function_decl|;
comment|/**      * Reverts all changes made to this root and refreshed to the latest trunk.      * After a call to this method, all trees obtained through {@link #getTree(String)}      * become invalid and fresh instances must be obtained.      */
name|void
name|refresh
parameter_list|()
function_decl|;
comment|/**      * Atomically apply all changes made to the tree beneath this root to the      * underlying store and refreshes this root. After a call to this method,      * all trees obtained through {@link #getTree(String)} become invalid and fresh      * instances must be obtained.      *      * @throws CommitFailedException      */
name|void
name|commit
parameter_list|()
throws|throws
name|CommitFailedException
function_decl|;
comment|/**      * Determine whether there are changes on this tree      * @return  {@code true} iff this tree was modified      */
name|boolean
name|hasPendingChanges
parameter_list|()
function_decl|;
comment|/**      * Return a {@link ChangeExtractor} for this {@code Root}.      * @return a {@code ChangeExtractor}.      */
annotation|@
name|Nonnull
name|ChangeExtractor
name|getChangeExtractor
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

