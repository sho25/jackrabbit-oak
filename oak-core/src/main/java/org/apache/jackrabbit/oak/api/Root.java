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

begin_comment
comment|/**  * The root of a {@link Tree}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Root
block|{
comment|/**      * Move the child located at {@code sourcePath} to a child      * at {@code destPath}. Do nothing if either the source      * does not exist, the parent of the destination does not exist      * or the destination exists already. Both paths must resolve      * to a child located beneath this root.      *      * @param sourcePath source path relative to this root      * @param destPath destination path relative to this root      * @return  {@code true} on success, {@code false} otherwise.      */
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
comment|/**      * Copy the child located at {@code sourcePath} to a child      * at {@code destPath}. Do nothing if either the source      * does not exist, the parent of the destination does not exist      * or the destination exists already. Both paths must resolve      * to a child located in this root.      *      * @param sourcePath source path relative to this root      * @param destPath destination path relative to this root      * @return  {@code true} on success, {@code false} otherwise.      */
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
comment|/**      * Retrieve the {@code Tree} at the given {@code path}. The path must resolve to      * a tree in this root.      *      * @param path  path to the tree      * @return  tree at the given path or {@code null} if no such tree exists      */
annotation|@
name|CheckForNull
name|Tree
name|getTree
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
comment|/**      * Rebase this root to the latest revision.  After a call to this method,      * all trees obtained through {@link #getTree(String)} become invalid and fresh      * instances must be obtained.      *      * @param conflictHandler  {@link ConflictHandler} for resolving conflicts.      */
name|void
name|rebase
parameter_list|(
name|ConflictHandler
name|conflictHandler
parameter_list|)
function_decl|;
comment|/**      * Reverts all changes made to this root and refreshed to the latest trunk.      * After a call to this method, all trees obtained through {@link #getTree(String)}      * become invalid and fresh instances must be obtained.      */
name|void
name|refresh
parameter_list|()
function_decl|;
comment|/**      * Atomically apply all changes made to the tree beneath this root to the      * underlying store and refreshes this root. After a call to this method,      * all trees obtained through {@link #getTree(String)} become invalid and fresh      * instances must be obtained.      *      * @param conflictHandler  {@link ConflictHandler} for resolving conflicts.      * @throws CommitFailedException      */
name|void
name|commit
parameter_list|(
name|ConflictHandler
name|conflictHandler
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
comment|/**      * Determine whether there are changes on this tree      * @return  {@code true} iff this tree was modified      */
name|boolean
name|hasPendingChanges
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

