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
name|run
operator|.
name|osgi
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Dictionary
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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

begin_comment
comment|/**  * A wrapper around a dictionary access it as a Map  * Taken from org.apache.felix.utils.collections.DictionaryAsMap  */
end_comment

begin_class
class|class
name|DictionaryAsMap
parameter_list|<
name|U
parameter_list|,
name|V
parameter_list|>
extends|extends
name|AbstractMap
argument_list|<
name|U
argument_list|,
name|V
argument_list|>
block|{
specifier|private
name|Dictionary
argument_list|<
name|U
argument_list|,
name|V
argument_list|>
name|dict
decl_stmt|;
specifier|public
name|DictionaryAsMap
parameter_list|(
name|Dictionary
argument_list|<
name|U
argument_list|,
name|V
argument_list|>
name|dict
parameter_list|)
block|{
name|this
operator|.
name|dict
operator|=
name|dict
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Entry
argument_list|<
name|U
argument_list|,
name|V
argument_list|>
argument_list|>
name|entrySet
parameter_list|()
block|{
return|return
operator|new
name|AbstractSet
argument_list|<
name|Entry
argument_list|<
name|U
argument_list|,
name|V
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|U
argument_list|,
name|V
argument_list|>
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|Enumeration
argument_list|<
name|U
argument_list|>
name|e
init|=
name|dict
operator|.
name|keys
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|U
argument_list|,
name|V
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|private
name|U
name|key
decl_stmt|;
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|e
operator|.
name|hasMoreElements
argument_list|()
return|;
block|}
specifier|public
name|Entry
argument_list|<
name|U
argument_list|,
name|V
argument_list|>
name|next
parameter_list|()
block|{
name|key
operator|=
name|e
operator|.
name|nextElement
argument_list|()
expr_stmt|;
return|return
operator|new
name|KeyEntry
argument_list|(
name|key
argument_list|)
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
name|dict
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|dict
operator|.
name|size
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|V
name|put
parameter_list|(
name|U
name|key
parameter_list|,
name|V
name|value
parameter_list|)
block|{
return|return
name|dict
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
class|class
name|KeyEntry
implements|implements
name|Map
operator|.
name|Entry
argument_list|<
name|U
argument_list|,
name|V
argument_list|>
block|{
specifier|private
specifier|final
name|U
name|key
decl_stmt|;
name|KeyEntry
parameter_list|(
name|U
name|key
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
specifier|public
name|U
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
specifier|public
name|V
name|getValue
parameter_list|()
block|{
return|return
name|dict
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
specifier|public
name|V
name|setValue
parameter_list|(
name|V
name|value
parameter_list|)
block|{
return|return
name|DictionaryAsMap
operator|.
name|this
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

