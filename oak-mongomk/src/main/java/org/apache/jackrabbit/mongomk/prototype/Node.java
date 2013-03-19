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
name|prototype
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

begin_comment
comment|/**  * Represents a node held in memory (in the cache for example).  */
end_comment

begin_class
specifier|public
class|class
name|Node
block|{
specifier|final
name|String
name|path
decl_stmt|;
specifier|final
name|Revision
name|rev
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
name|Utils
operator|.
name|newMap
argument_list|()
decl_stmt|;
specifier|private
name|long
name|writeCount
decl_stmt|;
name|Node
parameter_list|(
name|String
name|path
parameter_list|,
name|Revision
name|rev
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|rev
operator|=
name|rev
expr_stmt|;
block|}
name|void
name|setProperty
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|properties
operator|.
name|remove
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|properties
operator|.
name|put
argument_list|(
name|propertyName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
return|return
name|properties
operator|.
name|get
argument_list|(
name|propertyName
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getPropertyNames
parameter_list|()
block|{
return|return
name|properties
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|public
name|void
name|copyTo
parameter_list|(
name|Node
name|newNode
parameter_list|)
block|{
name|newNode
operator|.
name|properties
operator|.
name|putAll
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"path: "
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"rev: "
argument_list|)
operator|.
name|append
argument_list|(
name|rev
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"writeCount: "
argument_list|)
operator|.
name|append
argument_list|(
name|writeCount
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Create an add node operation for this node.      */
name|UpdateOp
name|asOperation
parameter_list|(
name|boolean
name|isNew
parameter_list|)
block|{
name|String
name|id
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|UpdateOp
name|op
init|=
operator|new
name|UpdateOp
argument_list|(
name|path
argument_list|,
name|id
argument_list|,
name|isNew
argument_list|)
decl_stmt|;
name|op
operator|.
name|set
argument_list|(
name|UpdateOp
operator|.
name|ID
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|addMapEntry
argument_list|(
name|UpdateOp
operator|.
name|DELETED
operator|+
literal|"."
operator|+
name|rev
operator|.
name|toString
argument_list|()
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|p
range|:
name|properties
operator|.
name|keySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|Utils
operator|.
name|escapePropertyName
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|op
operator|.
name|addMapEntry
argument_list|(
name|key
operator|+
literal|"."
operator|+
name|rev
operator|.
name|toString
argument_list|()
argument_list|,
name|properties
operator|.
name|get
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|op
return|;
block|}
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|path
operator|+
literal|"@"
operator|+
name|writeCount
return|;
block|}
specifier|public
name|void
name|append
parameter_list|(
name|JsopWriter
name|json
parameter_list|,
name|boolean
name|includeId
parameter_list|)
block|{
if|if
condition|(
name|includeId
condition|)
block|{
name|json
operator|.
name|key
argument_list|(
literal|":id"
argument_list|)
operator|.
name|value
argument_list|(
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|p
range|:
name|properties
operator|.
name|keySet
argument_list|()
control|)
block|{
name|json
operator|.
name|key
argument_list|(
name|p
argument_list|)
operator|.
name|encodedValue
argument_list|(
name|properties
operator|.
name|get
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setWriteCount
parameter_list|(
name|long
name|writeCount
parameter_list|)
block|{
name|this
operator|.
name|writeCount
operator|=
name|writeCount
expr_stmt|;
block|}
specifier|public
name|long
name|getWriteCount
parameter_list|()
block|{
return|return
name|writeCount
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|writeCount
operator|^
name|properties
operator|.
name|size
argument_list|()
operator|^
name|rev
operator|.
name|hashCode
argument_list|()
return|;
block|}
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
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|Node
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Node
name|other
init|=
operator|(
name|Node
operator|)
name|obj
decl_stmt|;
return|return
name|writeCount
operator|==
name|other
operator|.
name|writeCount
return|;
block|}
comment|/**      * A list of children for a node.      */
specifier|static
class|class
name|Children
block|{
specifier|final
name|String
name|path
decl_stmt|;
specifier|final
name|Revision
name|rev
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|String
argument_list|>
name|children
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Children
parameter_list|(
name|String
name|path
parameter_list|,
name|Revision
name|rev
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|rev
operator|=
name|rev
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|path
operator|+
literal|": "
operator|+
name|children
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

