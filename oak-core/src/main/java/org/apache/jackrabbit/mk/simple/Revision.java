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
name|mk
operator|.
name|simple
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
name|json
operator|.
name|JsopWriter
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
name|Cache
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
name|JsopTokenizer
import|;
end_import

begin_comment
comment|/**  * A revision, including pointer to the root node of that revision.  */
end_comment

begin_class
specifier|public
class|class
name|Revision
implements|implements
name|Comparable
argument_list|<
name|Revision
argument_list|>
implements|,
name|Cache
operator|.
name|Value
block|{
specifier|private
name|NodeImpl
name|node
decl_stmt|;
specifier|private
specifier|final
name|long
name|id
decl_stmt|;
specifier|private
specifier|final
name|long
name|nanos
decl_stmt|;
specifier|private
name|String
name|diff
decl_stmt|;
specifier|private
name|String
name|msg
decl_stmt|;
name|Revision
parameter_list|(
name|long
name|id
parameter_list|,
name|long
name|nanos
parameter_list|,
name|String
name|diff
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|nanos
operator|=
name|nanos
expr_stmt|;
name|this
operator|.
name|diff
operator|=
name|diff
expr_stmt|;
name|this
operator|.
name|msg
operator|=
name|msg
operator|==
literal|null
condition|?
literal|""
else|:
name|msg
expr_stmt|;
block|}
specifier|private
name|Revision
parameter_list|(
name|long
name|id
parameter_list|,
name|long
name|nanos
parameter_list|,
name|NodeImpl
name|node
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|nanos
operator|=
name|nanos
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
specifier|static
name|Revision
name|get
parameter_list|(
name|NodeImpl
name|node
parameter_list|)
block|{
name|String
name|rev
init|=
name|node
operator|.
name|getProperty
argument_list|(
literal|"rev"
argument_list|)
decl_stmt|;
if|if
condition|(
name|rev
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
name|id
init|=
name|parseId
argument_list|(
name|JsopTokenizer
operator|.
name|decodeQuoted
argument_list|(
name|rev
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|nanos
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|node
operator|.
name|getProperty
argument_list|(
literal|"nanos"
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|Revision
argument_list|(
name|id
argument_list|,
name|nanos
argument_list|,
name|node
argument_list|)
return|;
block|}
name|long
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
name|long
name|getNanos
parameter_list|()
block|{
return|return
name|nanos
return|;
block|}
specifier|private
name|String
name|getDiff
parameter_list|()
block|{
if|if
condition|(
name|diff
operator|==
literal|null
condition|)
block|{
name|String
name|s
init|=
name|getCommitValue
argument_list|(
literal|"diff"
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|s
operator|=
literal|""
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'['
condition|)
block|{
comment|// remove the surrounding "[" and "]"
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|s
operator|=
name|JsopTokenizer
operator|.
name|decodeQuoted
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|diff
operator|=
name|s
expr_stmt|;
block|}
return|return
name|diff
return|;
block|}
specifier|private
name|String
name|getMsg
parameter_list|()
block|{
if|if
condition|(
name|msg
operator|==
literal|null
condition|)
block|{
name|String
name|m
init|=
name|getCommitValue
argument_list|(
literal|"msg"
argument_list|)
decl_stmt|;
name|msg
operator|=
name|m
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|?
literal|""
else|:
name|JsopTokenizer
operator|.
name|decodeQuoted
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
return|return
name|msg
return|;
block|}
specifier|private
name|String
name|getCommitValue
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|exists
argument_list|(
literal|"commit"
argument_list|)
condition|)
block|{
name|String
name|v
init|=
name|node
operator|.
name|getNode
argument_list|(
literal|"commit"
argument_list|)
operator|.
name|getProperty
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
return|return
name|v
return|;
block|}
block|}
return|return
literal|""
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Revision
name|o
parameter_list|)
block|{
return|return
name|id
operator|<
name|o
operator|.
name|id
condition|?
operator|-
literal|1
else|:
name|id
operator|>
name|o
operator|.
name|id
condition|?
literal|1
else|:
literal|0
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|JsopBuilder
argument_list|()
operator|.
name|object
argument_list|()
operator|.
name|key
argument_list|(
literal|"id"
argument_list|)
operator|.
name|value
argument_list|(
name|formatId
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|key
argument_list|(
literal|"ts"
argument_list|)
operator|.
name|value
argument_list|(
name|nanos
operator|/
literal|1000000
argument_list|)
operator|.
name|key
argument_list|(
literal|"msg"
argument_list|)
operator|.
name|value
argument_list|(
name|getMsg
argument_list|()
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|static
name|long
name|parseId
parameter_list|(
name|String
name|revisionId
parameter_list|)
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|revisionId
argument_list|,
literal|16
argument_list|)
return|;
block|}
specifier|static
name|String
name|formatId
parameter_list|(
name|long
name|revId
parameter_list|)
block|{
return|return
name|Long
operator|.
name|toHexString
argument_list|(
name|revId
argument_list|)
return|;
block|}
name|NodeImpl
name|store
parameter_list|(
name|NodeImpl
name|head
parameter_list|,
name|NodeImpl
name|commit
parameter_list|)
block|{
name|head
operator|=
name|head
operator|.
name|cloneAndSetProperty
argument_list|(
literal|"rev"
argument_list|,
name|JsopBuilder
operator|.
name|encode
argument_list|(
name|formatId
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|head
operator|=
name|head
operator|.
name|cloneAndSetProperty
argument_list|(
literal|"nanos"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|nanos
argument_list|)
argument_list|,
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|diff
operator|.
name|indexOf
argument_list|(
literal|";\n"
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|commit
operator|.
name|setProperty
argument_list|(
literal|"diff"
argument_list|,
name|JsopBuilder
operator|.
name|encode
argument_list|(
name|diff
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|commit
operator|.
name|setProperty
argument_list|(
literal|"diff"
argument_list|,
literal|"["
operator|+
name|diff
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|msg
operator|!=
literal|null
operator|&&
name|msg
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|commit
operator|.
name|setProperty
argument_list|(
literal|"msg"
argument_list|,
name|JsopBuilder
operator|.
name|encode
argument_list|(
name|msg
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|head
operator|.
name|setChild
argument_list|(
literal|"commit"
argument_list|,
name|commit
argument_list|,
name|id
argument_list|)
return|;
block|}
name|void
name|appendJournal
parameter_list|(
name|JsopWriter
name|buff
parameter_list|)
block|{
name|buff
operator|.
name|object
argument_list|()
operator|.
name|key
argument_list|(
literal|"id"
argument_list|)
operator|.
name|value
argument_list|(
name|Revision
operator|.
name|formatId
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|key
argument_list|(
literal|"ts"
argument_list|)
operator|.
name|value
argument_list|(
name|nanos
operator|/
literal|1000000
argument_list|)
operator|.
name|key
argument_list|(
literal|"msg"
argument_list|)
operator|.
name|value
argument_list|(
name|getMsg
argument_list|()
argument_list|)
operator|.
name|key
argument_list|(
literal|"changes"
argument_list|)
operator|.
name|value
argument_list|(
name|getDiff
argument_list|()
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getMemory
parameter_list|()
block|{
return|return
operator|(
name|getDiff
argument_list|()
operator|.
name|length
argument_list|()
operator|+
name|getMsg
argument_list|()
operator|.
name|length
argument_list|()
operator|)
operator|*
literal|2
return|;
block|}
block|}
end_class

end_unit

