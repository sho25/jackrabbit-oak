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
name|document
package|;
end_package

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
name|SortedMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import static
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
operator|.
name|Checkpoints
operator|.
name|Info
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
name|document
operator|.
name|util
operator|.
name|Utils
import|;
end_import

begin_comment
comment|/**  * Helper class to access package private functionality.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|CheckpointsHelper
block|{
specifier|public
specifier|static
name|SortedMap
argument_list|<
name|Revision
argument_list|,
name|Long
argument_list|>
name|getCheckpoints
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|)
block|{
name|SortedMap
argument_list|<
name|Revision
argument_list|,
name|Info
argument_list|>
name|checkpoints
init|=
name|store
operator|.
name|getCheckpoints
argument_list|()
operator|.
name|getCheckpoints
argument_list|()
decl_stmt|;
name|SortedMap
argument_list|<
name|Revision
argument_list|,
name|Long
argument_list|>
name|map
init|=
name|Maps
operator|.
name|newTreeMap
argument_list|(
name|checkpoints
operator|.
name|comparator
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|Info
argument_list|>
name|entry
range|:
name|checkpoints
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|map
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
operator|.
name|getExpiryTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
specifier|public
specifier|static
name|long
name|removeAll
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|)
block|{
name|long
name|cnt
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Revision
name|r
range|:
name|getCheckpoints
argument_list|(
name|store
argument_list|)
operator|.
name|keySet
argument_list|()
control|)
block|{
name|store
operator|.
name|getCheckpoints
argument_list|()
operator|.
name|release
argument_list|(
name|r
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|cnt
operator|++
expr_stmt|;
block|}
return|return
name|cnt
return|;
block|}
specifier|public
specifier|static
name|long
name|removeOlderThan
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|,
name|Revision
name|r
parameter_list|)
block|{
name|long
name|cnt
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Revision
name|cp
range|:
name|getCheckpoints
argument_list|(
name|store
argument_list|)
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|cp
operator|.
name|getTimestamp
argument_list|()
operator|<
name|r
operator|.
name|getTimestamp
argument_list|()
condition|)
block|{
name|store
operator|.
name|getCheckpoints
argument_list|()
operator|.
name|release
argument_list|(
name|cp
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|cnt
operator|++
expr_stmt|;
block|}
block|}
return|return
name|cnt
return|;
block|}
specifier|public
specifier|static
name|int
name|remove
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|,
name|Revision
name|r
parameter_list|)
block|{
if|if
condition|(
name|getCheckpoints
argument_list|(
name|store
argument_list|)
operator|.
name|containsKey
argument_list|(
name|r
argument_list|)
condition|)
block|{
name|store
operator|.
name|getCheckpoints
argument_list|()
operator|.
name|release
argument_list|(
name|r
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
specifier|public
specifier|static
name|Revision
name|min
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|revs
parameter_list|)
block|{
if|if
condition|(
name|revs
operator|==
literal|null
operator|||
name|revs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Revision
name|r
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|cp
range|:
name|revs
control|)
block|{
name|r
operator|=
name|Utils
operator|.
name|min
argument_list|(
name|r
argument_list|,
name|Revision
operator|.
name|fromString
argument_list|(
name|cp
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
specifier|public
specifier|static
name|int
name|setInfoProperty
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|,
name|String
name|rev
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
try|try
block|{
name|store
operator|.
name|getCheckpoints
argument_list|()
operator|.
name|setInfoProperty
argument_list|(
name|rev
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
return|return
literal|1
return|;
block|}
block|}
end_class

end_unit

