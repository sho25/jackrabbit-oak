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
name|plugins
operator|.
name|document
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
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * A cache for child node diffs.  */
end_comment

begin_interface
specifier|public
interface|interface
name|DiffCache
block|{
comment|/**      * Returns a jsop diff for the child nodes at the given path. The returned      * String may contain the following changes on child nodes:      *<ul>      *<li>Changed child nodes: e.g. {@code ^"foo":{}}</li>      *<li>Added child nodes: e.g. {@code +"bar":{}}</li>      *<li>Removed child nodes: e.g. {@code -"baz"}</li>      *</ul>      * A {@code null} value indicates that this cache does not have an entry      * for the given revision range at the path.      *      * @param from the from revision.      * @param to the to revision.      * @param path the path of the parent node.      * @param loader an optional loader for the cache entry.      * @return the diff or {@code null} if unknown and no loader was passed.      */
annotation|@
name|CheckForNull
name|String
name|getChanges
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|from
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|to
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nullable
name|Loader
name|loader
parameter_list|)
function_decl|;
comment|/**      * Starts a new cache entry for the diff cache. Actual changes are added      * to the entry with the {@link Entry#append(String, String)} method.      *      * @param from the from revision.      * @param to the to revision.      * @return the cache entry.      */
annotation|@
name|Nonnull
name|Entry
name|newEntry
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|from
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|to
parameter_list|)
function_decl|;
specifier|public
interface|interface
name|Entry
block|{
comment|/**          * Appends changes about children of the node at the given path.          *          * @param path the path of the parent node.          * @param changes the child node changes.          */
name|void
name|append
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|String
name|changes
parameter_list|)
function_decl|;
comment|/**          * Called when all changes have been appended and the entry is ready          * to be used by the cache.          */
name|void
name|done
parameter_list|()
function_decl|;
block|}
specifier|public
interface|interface
name|Loader
block|{
name|String
name|call
parameter_list|()
function_decl|;
block|}
block|}
end_interface

end_unit

