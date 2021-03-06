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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_comment
comment|/**  * Helper class used to keep track of uuid mappings (e.g. if the uuid of an  * imported or copied node is mapped to a new uuid) and processed (e.g. imported  * or copied) reference properties that might need to be adjusted depending on  * the UUID mapping resulting from the import.  *  * @see javax.jcr.ImportUUIDBehavior  */
end_comment

begin_class
specifier|public
class|class
name|ReferenceChangeTracker
block|{
comment|/**      * mapping from original uuid to new uuid of mix:referenceable nodes      */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|uuidMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * list of processed reference properties that might need correcting      */
specifier|private
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|references
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Returns the new node id to which {@code oldUUID} has been mapped      * or {@code null} if no such mapping exists.      *      * @param oldUUID old node id      * @return mapped new id or {@code null} if no such mapping exists      * @see #put(String, String)      */
specifier|public
name|String
name|get
parameter_list|(
name|String
name|oldUUID
parameter_list|)
block|{
return|return
name|uuidMap
operator|.
name|get
argument_list|(
name|oldUUID
argument_list|)
return|;
block|}
comment|/**      * Store the given id mapping for later lookup using      * {@link #get(String)}.      *      * @param oldUUID old node id      * @param newUUID new node id      */
specifier|public
name|void
name|put
parameter_list|(
name|String
name|oldUUID
parameter_list|,
name|String
name|newUUID
parameter_list|)
block|{
name|uuidMap
operator|.
name|put
argument_list|(
name|oldUUID
argument_list|,
name|newUUID
argument_list|)
expr_stmt|;
block|}
comment|/**      * Resets all internal state.      */
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|uuidMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|references
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * Store the given reference property for later retrieval using      * {@link #getProcessedReferences()}.      *      * @param refProp reference property      */
specifier|public
name|void
name|processedReference
parameter_list|(
name|Object
name|refProp
parameter_list|)
block|{
name|references
operator|.
name|add
argument_list|(
name|refProp
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns an iterator over all processed reference properties.      *      * @return an iterator over all processed reference properties      * @see #processedReference(Object)      */
specifier|public
name|Iterator
argument_list|<
name|Object
argument_list|>
name|getProcessedReferences
parameter_list|()
block|{
return|return
name|references
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**      * Remove the given references that have already been processed from the      * references list.      *      * @param processedReferences      * @return {@code true} if the internal list of references changed.      */
specifier|public
name|boolean
name|removeReferences
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|processedReferences
parameter_list|)
block|{
return|return
name|references
operator|.
name|removeAll
argument_list|(
name|processedReferences
argument_list|)
return|;
block|}
block|}
end_class

end_unit

