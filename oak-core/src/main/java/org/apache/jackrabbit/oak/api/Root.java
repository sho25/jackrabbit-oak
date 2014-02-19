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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

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
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * A {@code Root} instance serves as a container for a {@link Tree}. It is  * obtained from a {@link ContentSession}, which governs accessibility and  * visibility of the {@code Tree} and its sub trees.  *<p>  * All root instances created by a content session become invalid after the  * content session is closed. Any method called on an invalid root instance  * will throw an {@code InvalidStateException}.  *<p>  * {@link Tree} instances may become non existing after a call to  * {@link #refresh()}, {@link #rebase()} or {@link #commit()}.  * Any write access to non existing {@code Tree} instances will cause an  * {@code InvalidStateException}.  * @see Tree Existence and iterability of trees  */
end_comment

begin_interface
specifier|public
interface|interface
name|Root
block|{
name|String
name|COMMIT_PATH
init|=
literal|"path"
decl_stmt|;
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
comment|/**      * Retrieve the possible non existing {@code Tree} at the given absolute {@code path}.      * The path must resolve to a tree in this root.      *      * @param path absolute path to the tree      * @return tree at the given path.      */
annotation|@
name|Nonnull
name|Tree
name|getTree
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
function_decl|;
comment|/**      * Rebase this root instance to the latest revision. After a call to this method,      * trees obtained through {@link #getTree(String)} may become non existing.      */
name|void
name|rebase
parameter_list|()
function_decl|;
comment|/**      * Reverts all changes made to this root and refreshed to the latest trunk.      * After a call to this method, trees obtained through {@link #getTree(String)}      * may become non existing.      */
name|void
name|refresh
parameter_list|()
function_decl|;
name|void
name|commit
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|info
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
comment|/**      * Atomically persists all changes made to the tree attached to this root      * at the given {@code path}. An implementations may throw a      * {@code CommitFailedException} if there are changes outside of the subtree      * designated by {@code path} and the implementation does not support      * such partial commits. However all implementation must handler the      * case where a {@code path} designates a subtree that contains all      * unpersisted changes.      *<p>      * The message string (if given) is passed to the underlying storage      * as a part of the internal commit information attached to this commit.      * The commit information will be made available to local observers but      * will not be visible to observers on other cluster nodes.      *<p>      * After a successful operation the root is automatically      * {@link #refresh() refreshed}, such that trees previously obtained      * through {@link #getTree(String)} may become non existing.      *      * @param message custom message to be associated with this commit      * @param path of the subtree to commit      * @throws CommitFailedException if the commit failed      */
name|void
name|commit
parameter_list|(
annotation|@
name|Nullable
name|String
name|message
parameter_list|,
annotation|@
name|Nullable
name|String
name|path
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
comment|/**      * Atomically persists all changes made to the tree attached to this root.      * Calling this method is equivalent to calling the      * {@link #commit(String, String)} method with {@code null} parameters for      * {@code message} and {@code path}.      *      * @throws CommitFailedException if the commit failed      */
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
comment|/**      * Get the query engine.      *       * @return the query engine      */
annotation|@
name|Nonnull
name|QueryEngine
name|getQueryEngine
parameter_list|()
function_decl|;
comment|/**      * Reads (and closes) the given stream and returns a {@link Blob} that      * contains that binary. The returned blob will remain valid at least      * until the {@link ContentSession} of this root is closed, or longer      * if it has been committed as a part of a content update.      *<p>      * The implementation may decide to persist the blob at any point      * during or between this method method call and a {@link #commit()}      * that includes the blob, but the blob will become visible to other      * sessions only after such a commit.      *      * @param stream the stream for reading the binary      * @return the blob that was created      * @throws IOException if the stream could not be read      */
annotation|@
name|Nonnull
name|Blob
name|createBlob
parameter_list|(
annotation|@
name|Nonnull
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Get a blob by its reference.      * @param reference  reference to the blob      * @return  blob or {@code null} if the reference does not resolve to a blob.      * @see Blob#getReference()      */
annotation|@
name|CheckForNull
name|Blob
name|getBlob
parameter_list|(
annotation|@
name|Nonnull
name|String
name|reference
parameter_list|)
function_decl|;
comment|/**      * Get the {@code ContentSession} from which this root was acquired      *       * @return the associated ContentSession      *       * @throws UnsupportedOperationException      */
annotation|@
name|Nonnull
name|ContentSession
name|getContentSession
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

