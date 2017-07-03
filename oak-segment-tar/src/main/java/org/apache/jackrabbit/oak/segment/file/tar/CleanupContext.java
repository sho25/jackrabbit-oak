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
name|segment
operator|.
name|file
operator|.
name|tar
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  * Initial data and logic needed for the cleanup of unused TAR entries.  */
end_comment

begin_interface
specifier|public
interface|interface
name|CleanupContext
block|{
comment|/**      * Initial references to TAR entries. These references represent the entries      * that are currently in use. The transitive closure of these entries will      * be computed by the cleanup algorithm.      *      * @return An instance of {@link Collection}.      */
name|Collection
argument_list|<
name|UUID
argument_list|>
name|initialReferences
parameter_list|()
function_decl|;
comment|/**      * Check if an entry should be reclaimed.      *      * @param id         The identifier of the entry.      * @param generation The generation of the entry.      * @param referenced If this entry was referenced directly or indirectly by      *                   the initial set of references.      * @return {@code true} if the entry should be reclaimed, {@code false}      * otherwise.      */
name|boolean
name|shouldReclaim
parameter_list|(
name|UUID
name|id
parameter_list|,
name|int
name|generation
parameter_list|,
name|boolean
name|referenced
parameter_list|)
function_decl|;
comment|/**      * Determine if a reference between two entries should be followed, and if      * the referenced entry should be marked.      *      * @param from The identifier of the referencing entry.      * @param to   The identifier of the referenced entry.      * @return {@code true} if the reference should be followed, {@code false}      * otherwise.      */
name|boolean
name|shouldFollow
parameter_list|(
name|UUID
name|from
parameter_list|,
name|UUID
name|to
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

