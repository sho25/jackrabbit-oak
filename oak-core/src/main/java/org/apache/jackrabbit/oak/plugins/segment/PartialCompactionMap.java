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
name|segment
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
comment|/**  * A {@code PartialCompactionMap} maps uncompacted to compacted record ids  * from a single compaction cycle.  *  * @see CompactionMap  */
end_comment

begin_interface
specifier|public
interface|interface
name|PartialCompactionMap
block|{
comment|/**      * Checks whether the record with the given {@code before} identifier was      * compacted to a new record with the given {@code after} identifier.      *      * @param before before record identifier      * @param after after record identifier      * @return whether {@code before} was compacted to {@code after}      */
name|boolean
name|wasCompactedTo
parameter_list|(
annotation|@
name|Nonnull
name|RecordId
name|before
parameter_list|,
annotation|@
name|Nonnull
name|RecordId
name|after
parameter_list|)
function_decl|;
comment|/**      * Checks whether content in the segment with the given identifier was      * compacted to new segments.      *      * @param id segment identifier      * @return whether the identified segment was compacted      */
name|boolean
name|wasCompacted
parameter_list|(
annotation|@
name|Nonnull
name|UUID
name|id
parameter_list|)
function_decl|;
comment|/**      * Retrieve the record id {@code before} maps to or {@code null}      * if no such id exists.      * @param before before record id      * @return after record id or {@code null}      */
annotation|@
name|CheckForNull
name|RecordId
name|get
parameter_list|(
annotation|@
name|Nonnull
name|RecordId
name|before
parameter_list|)
function_decl|;
comment|/**      * Adds a new entry to the compaction map. Overwriting a previously      * added entry is not supported.      * @param before  before record id      * @param after  after record id      * @throws IllegalArgumentException  if {@code before} already exists in the map      */
name|void
name|put
parameter_list|(
annotation|@
name|Nonnull
name|RecordId
name|before
parameter_list|,
annotation|@
name|Nonnull
name|RecordId
name|after
parameter_list|)
function_decl|;
comment|/**      * Remove all keys from this map where {@code keys.contains(key.asUUID())}.      * @param uuids  uuids of the keys to remove      */
name|void
name|remove
parameter_list|(
annotation|@
name|Nonnull
name|Set
argument_list|<
name|UUID
argument_list|>
name|uuids
parameter_list|)
function_decl|;
comment|/**      * Compressing this map ensures it takes up as little heap as possible. This      * operation might be expensive and should only be called in suitable intervals.      */
name|void
name|compress
parameter_list|()
function_decl|;
comment|/**      * Number of segments referenced by the keys in this map. The returned value might only      * be based on the compressed part of the map.      * @return  number of segments      */
name|long
name|getSegmentCount
parameter_list|()
function_decl|;
comment|/**      * Number of records referenced by the keys in this map. The returned value might only      * be based on the compressed part of the map.      * @return  number of records      */
name|long
name|getRecordCount
parameter_list|()
function_decl|;
comment|/**      * The weight of the compaction map is its heap memory consumption in bytes.      * @return  Estimated weight of the compaction map      */
name|long
name|getEstimatedWeight
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

