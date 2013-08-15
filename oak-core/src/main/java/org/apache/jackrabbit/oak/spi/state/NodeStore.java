begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|state
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
name|Blob
import|;
end_import

begin_comment
comment|/**  * Storage abstraction for trees. At any given point in time the stored  * tree is rooted at a single immutable node state.  *<p>  * This is a low-level interface that doesn't cover functionality like  * merging concurrent changes or rejecting new tree states based on some  * higher-level consistency constraints.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeStore
block|{
comment|/**      * Returns the latest state of the tree.      *      * @return root node state      */
annotation|@
name|Nonnull
name|NodeState
name|getRoot
parameter_list|()
function_decl|;
comment|/**      * Creates a new branch of the tree to which transient changes can be applied.      *      * @return branch      */
annotation|@
name|Nonnull
name|NodeStoreBranch
name|branch
parameter_list|()
function_decl|;
comment|/**      * Create a {@link Blob} from the given input stream. The input stream      * is closed after this method returns.      * @param inputStream  The input stream for the {@code Blob}      * @return  The {@code Blob} representing {@code inputStream}      * @throws IOException  If an error occurs while reading from the stream      */
name|Blob
name|createBlob
parameter_list|(
name|InputStream
name|inputStream
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Creates a new checkpoint of the latest root of the tree. The checkpoint      * remains valid for at least as long as requested and allows that state      * of the repository to be retrieved using the returned opaque string      * reference.      *      * @param lifetime time (in milliseconds,&gt; 0) that the checkpoint      *                 should remain available      * @return string reference of this checkpoint      */
annotation|@
name|Nonnull
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|)
function_decl|;
comment|/**      * Retrieves the root node from a previously created repository checkpoint.      *      * @param checkpoint string reference of a checkpoint      * @return the root node of the checkpoint,      *         or {@code null} if the checkpoint is no longer available      */
annotation|@
name|CheckForNull
name|NodeState
name|retrieve
parameter_list|(
annotation|@
name|Nonnull
name|String
name|checkpoint
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

