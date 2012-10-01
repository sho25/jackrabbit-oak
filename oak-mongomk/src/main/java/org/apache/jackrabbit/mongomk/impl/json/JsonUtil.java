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
name|mongomk
operator|.
name|impl
operator|.
name|json
package|;
end_package

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
name|LinkedList
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
name|json
operator|.
name|JsopBuilder
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
name|NodeFilter
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
name|mongomk
operator|.
name|api
operator|.
name|model
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|JSONArray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|JSONObject
import|;
end_import

begin_comment
comment|/**  * FIXME - [Mete] This should really merge with MicroKernelImpl#toJson.  *  *<a href="http://en.wikipedia.org/wiki/JavaScript_Object_Notation">JSON</a> related utility classes.  *  * @author<a href="mailto:pmarx@adobe.com>Philipp Marx</a>  */
end_comment

begin_class
specifier|public
class|class
name|JsonUtil
block|{
specifier|public
specifier|static
name|Object
name|convertJsonValue
parameter_list|(
name|String
name|jsonValue
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|jsonValue
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|dummyJson
init|=
literal|"{dummy : "
operator|+
name|jsonValue
operator|+
literal|"}"
decl_stmt|;
name|JSONObject
name|jsonObject
init|=
operator|new
name|JSONObject
argument_list|(
name|dummyJson
argument_list|)
decl_stmt|;
name|Object
name|dummyObject
init|=
name|jsonObject
operator|.
name|get
argument_list|(
literal|"dummy"
argument_list|)
decl_stmt|;
return|return
name|convertJsonValue
argument_list|(
name|dummyObject
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|convertToJson
parameter_list|(
name|Node
name|node
parameter_list|,
name|int
name|depth
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|maxChildNodes
parameter_list|,
name|boolean
name|inclVirtualProps
parameter_list|,
name|NodeFilter
name|filter
parameter_list|)
block|{
name|JsopBuilder
name|builder
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
name|convertToJson
argument_list|(
name|builder
argument_list|,
name|node
argument_list|,
name|depth
argument_list|,
literal|0
argument_list|,
name|offset
argument_list|,
name|maxChildNodes
argument_list|,
name|inclVirtualProps
argument_list|,
name|filter
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|static
name|void
name|convertToJson
parameter_list|(
name|JsopBuilder
name|builder
parameter_list|,
name|Node
name|node
parameter_list|,
name|int
name|depth
parameter_list|,
name|int
name|currentDepth
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|maxChildNodes
parameter_list|,
name|boolean
name|inclVirtualProps
parameter_list|,
name|NodeFilter
name|filter
parameter_list|)
block|{
name|builder
operator|.
name|object
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
init|=
name|node
operator|.
name|getProperties
argument_list|()
decl_stmt|;
if|if
condition|(
name|properties
operator|!=
literal|null
condition|)
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
name|properties
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|filter
operator|==
literal|null
operator|||
name|filter
operator|.
name|includeProperty
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|Object
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|builder
operator|.
name|key
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|encodedValue
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|long
name|childCount
init|=
name|node
operator|.
name|getChildCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|inclVirtualProps
condition|)
block|{
if|if
condition|(
name|filter
operator|==
literal|null
operator|||
name|filter
operator|.
name|includeProperty
argument_list|(
literal|":childNodeCount"
argument_list|)
condition|)
block|{
comment|// :childNodeCount is by default always included
comment|// unless it is explicitly excluded in the filter
name|builder
operator|.
name|key
argument_list|(
literal|":childNodeCount"
argument_list|)
operator|.
name|value
argument_list|(
name|childCount
argument_list|)
expr_stmt|;
block|}
comment|// FIXME [Mete] See if :hash is still being used.
comment|/*check whether :hash has been explicitly included             if (filter != null) {                 NameFilter nf = filter.getPropertyFilter();                 if (nf != null&& nf.getInclusionPatterns().contains(":hash")&& !nf.getExclusionPatterns().contains(":hash")) {                     builder.key(":hash").value(rep.getRevisionStore().getId(node).toString());                 }             }             */
block|}
comment|// FIXME [Mete] There's still some more work here.
name|Iterator
argument_list|<
name|Node
argument_list|>
name|entries
init|=
name|node
operator|.
name|getChildEntries
argument_list|(
name|offset
argument_list|,
name|maxChildNodes
argument_list|)
decl_stmt|;
while|while
condition|(
name|entries
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Node
name|child
init|=
name|entries
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|numSiblings
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|maxChildNodes
operator|!=
operator|-
literal|1
operator|&&
operator|++
name|numSiblings
operator|>
name|maxChildNodes
condition|)
block|{
break|break;
block|}
name|builder
operator|.
name|key
argument_list|(
name|child
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|depth
operator|==
operator|-
literal|1
operator|)
operator|||
operator|(
name|currentDepth
operator|<
name|depth
operator|)
condition|)
block|{
name|convertToJson
argument_list|(
name|builder
argument_list|,
name|child
argument_list|,
name|depth
argument_list|,
name|currentDepth
operator|+
literal|1
argument_list|,
name|offset
argument_list|,
name|maxChildNodes
argument_list|,
name|inclVirtualProps
argument_list|,
name|filter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|object
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|Object
name|convertJsonValue
parameter_list|(
name|Object
name|jsonObject
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|jsonObject
operator|==
name|JSONObject
operator|.
name|NULL
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|jsonObject
operator|instanceof
name|JSONArray
condition|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|elements
init|=
operator|new
name|LinkedList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|JSONArray
name|dummyArray
init|=
operator|(
name|JSONArray
operator|)
name|jsonObject
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dummyArray
operator|.
name|length
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|Object
name|raw
init|=
name|dummyArray
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Object
name|parsed
init|=
name|convertJsonValue
argument_list|(
name|raw
argument_list|)
decl_stmt|;
name|elements
operator|.
name|add
argument_list|(
name|parsed
argument_list|)
expr_stmt|;
block|}
return|return
name|elements
return|;
block|}
return|return
name|jsonObject
return|;
block|}
specifier|private
name|JsonUtil
parameter_list|()
block|{
comment|// no instantiation
block|}
block|}
end_class

end_unit

