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
operator|.
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
comment|/**  * An index for the entries in a TAR file.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Index
block|{
comment|/**      * Returns the identifiers of every entry in this index.      *      * @return A set of {@link UUID}.      */
name|Set
argument_list|<
name|UUID
argument_list|>
name|getUUIDs
parameter_list|()
function_decl|;
comment|/**      * Find an entry by its identifier.      *      * @param msb The most significant bits of the identifier.      * @param lsb The least significant bits of the identifier.      * @return The index of the entry in this index, or {@code -1} if the entry      * was not found.      */
name|int
name|findEntry
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
function_decl|;
comment|/**      * Return the size of this index in bytes.      *      * @return The size of this index in bytes.      */
name|int
name|size
parameter_list|()
function_decl|;
comment|/**      * Return the number of entries in this index.      *      * @return The number of entries in this index.      */
name|int
name|count
parameter_list|()
function_decl|;
comment|/**      * Return the entry at a specified index.      *      * @param i The index of the entry.      * @return An instance of {@link IndexEntry}.      */
name|IndexEntry
name|entry
parameter_list|(
name|int
name|i
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

