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
name|query
operator|.
name|index
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
name|oak
operator|.
name|commons
operator|.
name|PathUtils
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
name|spi
operator|.
name|query
operator|.
name|Cursor
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
name|spi
operator|.
name|query
operator|.
name|Cursors
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
name|spi
operator|.
name|query
operator|.
name|Filter
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
name|spi
operator|.
name|query
operator|.
name|Filter
operator|.
name|PathRestriction
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
name|spi
operator|.
name|query
operator|.
name|QueryIndex
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
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * An index that traverses over a given subtree.  */
end_comment

begin_class
specifier|public
class|class
name|TraversingIndex
implements|implements
name|QueryIndex
block|{
annotation|@
name|Override
specifier|public
name|Cursor
name|query
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
block|{
return|return
name|Cursors
operator|.
name|newTraversingCursor
argument_list|(
name|filter
argument_list|,
name|rootState
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getCost
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|.
name|getFullTextConstraint
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// not an appropriate index for full-text search
return|return
name|Double
operator|.
name|POSITIVE_INFINITY
return|;
block|}
if|if
condition|(
name|filter
operator|.
name|containsNativeConstraint
argument_list|()
condition|)
block|{
comment|// not an appropriate index for native search
return|return
name|Double
operator|.
name|POSITIVE_INFINITY
return|;
block|}
if|if
condition|(
name|filter
operator|.
name|isAlwaysFalse
argument_list|()
condition|)
block|{
return|return
literal|0
return|;
block|}
comment|// worst case 100 million nodes
name|double
name|nodeCount
init|=
literal|100000000
decl_stmt|;
comment|// worst case 100 thousand children
name|double
name|nodeCountChildren
init|=
literal|100000
decl_stmt|;
comment|// if the path is from a join, then the depth is not correct
comment|// (the path might be the root node), but that's OK
name|String
name|path
init|=
name|filter
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|PathRestriction
name|restriction
init|=
name|filter
operator|.
name|getPathRestriction
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|restriction
condition|)
block|{
case|case
name|NO_RESTRICTION
case|:
break|break;
case|case
name|EXACT
case|:
name|nodeCount
operator|=
literal|1
expr_stmt|;
break|break;
case|case
name|ALL_CHILDREN
case|:
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|int
name|depth
init|=
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|path
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|depth
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
comment|// estimate 10 child nodes per node,
comment|// but higher than the cost for DIRECT_CHILDREN
comment|// (about 100'000)
comment|// in any case, the higher the depth of the path,
comment|// the lower the cost
name|nodeCount
operator|=
name|Math
operator|.
name|max
argument_list|(
name|nodeCountChildren
operator|*
literal|2
operator|-
name|depth
argument_list|,
name|nodeCount
operator|/
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|PARENT
case|:
if|if
condition|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|nodeCount
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|nodeCount
operator|=
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|DIRECT_CHILDREN
case|:
comment|// estimate 100'000 children for now,
comment|// to ensure an index is used if there is one
comment|// TODO we need to have better estimates, see also OAK-1898
name|nodeCount
operator|=
name|nodeCountChildren
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown restriction: "
operator|+
name|restriction
argument_list|)
throw|;
block|}
return|return
name|nodeCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPlan
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
block|{
return|return
literal|"traverse \""
operator|+
name|filter
operator|.
name|getPathPlan
argument_list|()
operator|+
literal|'"'
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIndexName
parameter_list|()
block|{
return|return
literal|"traverse"
return|;
block|}
block|}
end_class

end_unit

