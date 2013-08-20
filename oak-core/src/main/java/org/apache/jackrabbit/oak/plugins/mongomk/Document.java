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
name|mongomk
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|cache
operator|.
name|CacheValue
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
name|plugins
operator|.
name|mongomk
operator|.
name|util
operator|.
name|Utils
import|;
end_import

begin_comment
comment|/**  * A document corresponds to a node stored in the MongoMK. A document contains  * all the revisions of a node stored in the {@link DocumentStore}.  */
end_comment

begin_class
specifier|public
class|class
name|Document
implements|implements
name|CacheValue
block|{
comment|/**      * The node id, which contains the depth of the path      * (0 for root, 1 for children of the root), and then the path.      */
specifier|static
specifier|final
name|String
name|ID
init|=
literal|"_id"
decl_stmt|;
comment|/**      * The data of this document.      */
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|data
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Whether this document is sealed (immutable data).      */
specifier|private
name|AtomicBoolean
name|sealed
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|/**      * @return the id of this document or<code>null</code> if none is set.      */
annotation|@
name|CheckForNull
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
operator|(
name|String
operator|)
name|get
argument_list|(
name|ID
argument_list|)
return|;
block|}
comment|/**      * Gets the data for the given<code>key</code>.      *      * @param key the key.      * @return the data or<code>null</code>.      */
annotation|@
name|CheckForNull
specifier|public
name|Object
name|get
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|data
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**      * Sets the data for the given<code>key</code>.      *      * @param key the key.      * @param value the value to set.      * @return the previous value or<code>null</code> if there was none.      */
annotation|@
name|CheckForNull
specifier|public
name|Object
name|put
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
return|return
name|data
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * @return a Set view of the keys contained in this document.      */
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|keySet
parameter_list|()
block|{
return|return
name|data
operator|.
name|keySet
argument_list|()
return|;
block|}
comment|/**      * Seals this document and turns it into an immutable object. Any attempt      * to modify this document afterwards will result in an      * {@link UnsupportedOperationException}.      */
specifier|public
name|void
name|seal
parameter_list|()
block|{
if|if
condition|(
operator|!
name|sealed
operator|.
name|getAndSet
argument_list|(
literal|true
argument_list|)
condition|)
block|{
name|data
operator|=
name|seal
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Performs a deep copy of the data within this document to the given target.      *      * @param target the target document.      */
specifier|public
name|void
name|deepCopy
parameter_list|(
name|Document
name|target
parameter_list|)
block|{
name|Utils
operator|.
name|deepCopyMap
argument_list|(
name|data
argument_list|,
name|target
operator|.
name|data
argument_list|)
expr_stmt|;
block|}
comment|/**      * Formats this document for use in a log message.      *      * @return the formatted string      */
specifier|public
name|String
name|format
parameter_list|()
block|{
return|return
name|data
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|", _"
argument_list|,
literal|",\n_"
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"}, "
argument_list|,
literal|"},\n"
argument_list|)
return|;
block|}
comment|//-----------------------------< CacheValue>-------------------------------
annotation|@
name|Override
specifier|public
name|int
name|getMemory
parameter_list|()
block|{
return|return
name|Utils
operator|.
name|estimateMemoryUsage
argument_list|(
name|this
operator|.
name|data
argument_list|)
return|;
block|}
comment|//------------------------------< internal>--------------------------------
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|seal
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|Map
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|childMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|value
decl_stmt|;
name|entry
operator|.
name|setValue
argument_list|(
name|seal
argument_list|(
name|childMap
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|map
argument_list|)
return|;
block|}
block|}
end_class

end_unit

