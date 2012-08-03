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
name|mk
operator|.
name|model
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|store
operator|.
name|Binding
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
name|mk
operator|.
name|util
operator|.
name|AbstractFilteringIterator
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
name|mk
operator|.
name|util
operator|.
name|RangeIterator
import|;
end_import

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
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|ChildNodeEntriesMap
implements|implements
name|ChildNodeEntries
block|{
specifier|protected
specifier|static
specifier|final
name|List
argument_list|<
name|ChildNodeEntry
argument_list|>
name|EMPTY
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
specifier|protected
name|HashMap
argument_list|<
name|String
argument_list|,
name|ChildNodeEntry
argument_list|>
name|entries
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ChildNodeEntry
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|ChildNodeEntriesMap
parameter_list|()
block|{     }
specifier|public
name|ChildNodeEntriesMap
parameter_list|(
name|ChildNodeEntriesMap
name|other
parameter_list|)
block|{
name|entries
operator|=
operator|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|ChildNodeEntry
argument_list|>
operator|)
name|other
operator|.
name|entries
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
comment|//------------------------------------------------------------< overrides>
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|ChildNodeEntriesMap
condition|)
block|{
return|return
name|entries
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|ChildNodeEntriesMap
operator|)
name|obj
operator|)
operator|.
name|entries
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|ChildNodeEntriesMap
name|clone
init|=
literal|null
decl_stmt|;
try|try
block|{
name|clone
operator|=
operator|(
name|ChildNodeEntriesMap
operator|)
name|super
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
comment|// can't possibly get here
block|}
name|clone
operator|.
name|entries
operator|=
operator|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|ChildNodeEntry
argument_list|>
operator|)
name|entries
operator|.
name|clone
argument_list|()
expr_stmt|;
return|return
name|clone
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|inlined
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|//-------------------------------------------------------------< read ops>
annotation|@
name|Override
specifier|public
name|int
name|getCount
parameter_list|()
block|{
return|return
name|entries
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ChildNodeEntry
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|entries
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getNames
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|offset
operator|<
literal|0
operator|||
name|count
operator|<
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
if|if
condition|(
name|offset
operator|==
literal|0
operator|&&
name|count
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|entries
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
else|else
block|{
if|if
condition|(
name|offset
operator|>=
name|entries
operator|.
name|size
argument_list|()
operator|||
name|count
operator|==
literal|0
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|empty
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
return|return
name|empty
operator|.
name|iterator
argument_list|()
return|;
block|}
if|if
condition|(
name|count
operator|==
operator|-
literal|1
operator|||
operator|(
name|offset
operator|+
name|count
operator|)
operator|>
name|entries
operator|.
name|size
argument_list|()
condition|)
block|{
name|count
operator|=
name|entries
operator|.
name|size
argument_list|()
operator|-
name|offset
expr_stmt|;
block|}
return|return
operator|new
name|RangeIterator
argument_list|<
name|String
argument_list|>
argument_list|(
name|entries
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|,
name|offset
argument_list|,
name|count
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ChildNodeEntry
argument_list|>
name|getEntries
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|offset
operator|<
literal|0
operator|||
name|count
operator|<
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
if|if
condition|(
name|offset
operator|==
literal|0
operator|&&
name|count
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|entries
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
else|else
block|{
if|if
condition|(
name|offset
operator|>=
name|entries
operator|.
name|size
argument_list|()
operator|||
name|count
operator|==
literal|0
condition|)
block|{
return|return
name|EMPTY
operator|.
name|iterator
argument_list|()
return|;
block|}
if|if
condition|(
name|count
operator|==
operator|-
literal|1
operator|||
operator|(
name|offset
operator|+
name|count
operator|)
operator|>
name|entries
operator|.
name|size
argument_list|()
condition|)
block|{
name|count
operator|=
name|entries
operator|.
name|size
argument_list|()
operator|-
name|offset
expr_stmt|;
block|}
return|return
operator|new
name|RangeIterator
argument_list|<
name|ChildNodeEntry
argument_list|>
argument_list|(
name|entries
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|,
name|offset
argument_list|,
name|count
argument_list|)
return|;
block|}
block|}
comment|//------------------------------------------------------------< write ops>
annotation|@
name|Override
specifier|public
name|ChildNodeEntry
name|add
parameter_list|(
name|ChildNodeEntry
name|entry
parameter_list|)
block|{
return|return
name|entries
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|,
name|entry
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ChildNodeEntry
name|remove
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|entries
operator|.
name|remove
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ChildNodeEntry
name|rename
parameter_list|(
name|String
name|oldName
parameter_list|,
name|String
name|newName
parameter_list|)
block|{
if|if
condition|(
name|oldName
operator|.
name|equals
argument_list|(
name|newName
argument_list|)
condition|)
block|{
return|return
name|entries
operator|.
name|get
argument_list|(
name|oldName
argument_list|)
return|;
block|}
if|if
condition|(
name|entries
operator|.
name|get
argument_list|(
name|oldName
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|HashMap
argument_list|<
name|String
argument_list|,
name|ChildNodeEntry
argument_list|>
name|clone
init|=
operator|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|ChildNodeEntry
argument_list|>
operator|)
name|entries
operator|.
name|clone
argument_list|()
decl_stmt|;
name|entries
operator|.
name|clear
argument_list|()
expr_stmt|;
name|ChildNodeEntry
name|oldCNE
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ChildNodeEntry
argument_list|>
name|entry
range|:
name|clone
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|oldName
argument_list|)
condition|)
block|{
name|oldCNE
operator|=
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|entries
operator|.
name|put
argument_list|(
name|newName
argument_list|,
operator|new
name|ChildNodeEntry
argument_list|(
name|newName
argument_list|,
name|oldCNE
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|entries
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|oldCNE
return|;
block|}
comment|//-------------------------------------------------------------< diff ops>
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ChildNodeEntry
argument_list|>
name|getAdded
parameter_list|(
specifier|final
name|ChildNodeEntries
name|other
parameter_list|)
block|{
return|return
operator|new
name|AbstractFilteringIterator
argument_list|<
name|ChildNodeEntry
argument_list|>
argument_list|(
name|other
operator|.
name|getEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|include
parameter_list|(
name|ChildNodeEntry
name|entry
parameter_list|)
block|{
return|return
operator|!
name|entries
operator|.
name|containsKey
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ChildNodeEntry
argument_list|>
name|getRemoved
parameter_list|(
specifier|final
name|ChildNodeEntries
name|other
parameter_list|)
block|{
return|return
operator|new
name|AbstractFilteringIterator
argument_list|<
name|ChildNodeEntry
argument_list|>
argument_list|(
name|entries
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|include
parameter_list|(
name|ChildNodeEntry
name|entry
parameter_list|)
block|{
return|return
name|other
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
operator|==
literal|null
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ChildNodeEntry
argument_list|>
name|getModified
parameter_list|(
specifier|final
name|ChildNodeEntries
name|other
parameter_list|)
block|{
return|return
operator|new
name|AbstractFilteringIterator
argument_list|<
name|ChildNodeEntry
argument_list|>
argument_list|(
name|getEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|include
parameter_list|(
name|ChildNodeEntry
name|entry
parameter_list|)
block|{
name|ChildNodeEntry
name|namesake
init|=
name|other
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|(
name|namesake
operator|!=
literal|null
operator|&&
operator|!
name|namesake
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getId
argument_list|()
argument_list|)
operator|)
return|;
block|}
block|}
return|;
block|}
comment|//------------------------------------------------< serialization support>
annotation|@
name|Override
specifier|public
name|void
name|serialize
parameter_list|(
name|Binding
name|binding
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Iterator
argument_list|<
name|ChildNodeEntry
argument_list|>
name|iter
init|=
name|getEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|binding
operator|.
name|writeMap
argument_list|(
literal|":children"
argument_list|,
name|getCount
argument_list|()
argument_list|,
operator|new
name|Binding
operator|.
name|BytesEntryIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Binding
operator|.
name|BytesEntry
name|next
parameter_list|()
block|{
name|ChildNodeEntry
name|cne
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|new
name|Binding
operator|.
name|BytesEntry
argument_list|(
name|cne
operator|.
name|getName
argument_list|()
argument_list|,
name|cne
operator|.
name|getId
argument_list|()
operator|.
name|getBytes
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|ChildNodeEntriesMap
name|deserialize
parameter_list|(
name|Binding
name|binding
parameter_list|)
throws|throws
name|Exception
block|{
name|ChildNodeEntriesMap
name|newInstance
init|=
operator|new
name|ChildNodeEntriesMap
argument_list|()
decl_stmt|;
name|Binding
operator|.
name|BytesEntryIterator
name|iter
init|=
name|binding
operator|.
name|readBytesMap
argument_list|(
literal|":children"
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Binding
operator|.
name|BytesEntry
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|newInstance
operator|.
name|add
argument_list|(
operator|new
name|ChildNodeEntry
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|Id
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|newInstance
return|;
block|}
block|}
end_class

end_unit

