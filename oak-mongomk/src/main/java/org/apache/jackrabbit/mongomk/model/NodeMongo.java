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
name|model
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
name|Collection
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
name|HashSet
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
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|model
operator|.
name|NodeImpl
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
name|util
operator|.
name|MongoUtil
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
name|commons
operator|.
name|PathUtils
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|BasicDBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBObject
import|;
end_import

begin_comment
comment|/**  * The {@code MongoDB} representation of a node.  *  * @author<a href="mailto:pmarx@adobe.com>Philipp Marx</a>  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"javadoc"
argument_list|)
specifier|public
class|class
name|NodeMongo
extends|extends
name|BasicDBObject
block|{
specifier|public
specifier|static
specifier|final
name|String
name|KEY_BASE_REVISION_ID
init|=
literal|"baseRevId"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_CHILDREN
init|=
literal|"kids"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_PATH
init|=
literal|"path"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_PROPERTIES
init|=
literal|"props"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_REVISION_ID
init|=
literal|"revId"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|3153393934945155106L
decl_stmt|;
specifier|public
specifier|static
name|NodeMongo
name|fromDBObject
parameter_list|(
name|DBObject
name|node
parameter_list|)
block|{
name|NodeMongo
name|nodeMongo
init|=
operator|new
name|NodeMongo
argument_list|()
decl_stmt|;
name|nodeMongo
operator|.
name|putAll
argument_list|(
name|node
argument_list|)
expr_stmt|;
return|return
name|nodeMongo
return|;
block|}
specifier|public
specifier|static
name|NodeMongo
name|fromNode
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|NodeMongo
name|nodeMongo
init|=
operator|new
name|NodeMongo
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|node
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|nodeMongo
operator|.
name|setPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|String
name|revisionId
init|=
name|node
operator|.
name|getRevisionId
argument_list|()
decl_stmt|;
if|if
condition|(
name|revisionId
operator|!=
literal|null
condition|)
block|{
name|nodeMongo
operator|.
name|setRevisionId
argument_list|(
name|revisionId
argument_list|)
expr_stmt|;
block|}
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
name|nodeMongo
operator|.
name|setProperties
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|Node
argument_list|>
name|children
init|=
name|node
operator|.
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|childNames
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Node
name|child
range|:
name|children
control|)
block|{
name|childNames
operator|.
name|add
argument_list|(
name|child
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|nodeMongo
operator|.
name|setChildren
argument_list|(
name|childNames
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeMongo
return|;
block|}
specifier|public
specifier|static
name|Set
argument_list|<
name|NodeMongo
argument_list|>
name|fromNodes
parameter_list|(
name|Collection
argument_list|<
name|Node
argument_list|>
name|nodes
parameter_list|)
block|{
name|Set
argument_list|<
name|NodeMongo
argument_list|>
name|nodeMongos
init|=
operator|new
name|HashSet
argument_list|<
name|NodeMongo
argument_list|>
argument_list|(
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
name|NodeMongo
name|nodeMongo
init|=
name|NodeMongo
operator|.
name|fromNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|nodeMongos
operator|.
name|add
argument_list|(
name|nodeMongo
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeMongos
return|;
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|Node
argument_list|>
name|toNode
parameter_list|(
name|Collection
argument_list|<
name|NodeMongo
argument_list|>
name|nodeMongos
parameter_list|)
block|{
name|List
argument_list|<
name|Node
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
argument_list|<
name|Node
argument_list|>
argument_list|(
name|nodeMongos
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|NodeMongo
name|nodeMongo
range|:
name|nodeMongos
control|)
block|{
name|Node
name|node
init|=
name|NodeMongo
operator|.
name|toNode
argument_list|(
name|nodeMongo
argument_list|)
decl_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
return|return
name|nodes
return|;
block|}
specifier|public
specifier|static
name|NodeImpl
name|toNode
parameter_list|(
name|NodeMongo
name|nodeMongo
parameter_list|)
block|{
name|String
name|revisionId
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|nodeMongo
operator|.
name|getRevisionId
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|nodeMongo
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|childNames
init|=
name|nodeMongo
operator|.
name|getChildren
argument_list|()
decl_stmt|;
name|long
name|childCount
init|=
name|childNames
operator|!=
literal|null
condition|?
name|childNames
operator|.
name|size
argument_list|()
else|:
literal|0
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
init|=
name|nodeMongo
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Node
argument_list|>
name|children
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|childNames
operator|!=
literal|null
condition|)
block|{
name|children
operator|=
operator|new
name|HashSet
argument_list|<
name|Node
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|childName
range|:
name|childNames
control|)
block|{
name|NodeImpl
name|child
init|=
operator|new
name|NodeImpl
argument_list|()
decl_stmt|;
name|child
operator|.
name|setPath
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|childName
argument_list|)
argument_list|)
expr_stmt|;
name|children
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
name|NodeImpl
name|nodeImpl
init|=
operator|new
name|NodeImpl
argument_list|()
decl_stmt|;
name|nodeImpl
operator|.
name|setPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|nodeImpl
operator|.
name|setChildCount
argument_list|(
name|childCount
argument_list|)
expr_stmt|;
name|nodeImpl
operator|.
name|setRevisionId
argument_list|(
name|revisionId
argument_list|)
expr_stmt|;
name|nodeImpl
operator|.
name|setProperties
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|nodeImpl
operator|.
name|setChildren
argument_list|(
name|children
argument_list|)
expr_stmt|;
return|return
name|nodeImpl
return|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|addedChildren
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|addedProps
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|removedChildren
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|removedProps
decl_stmt|;
specifier|public
name|void
name|addChild
parameter_list|(
name|String
name|childName
parameter_list|)
block|{
if|if
condition|(
name|addedChildren
operator|==
literal|null
condition|)
block|{
name|addedChildren
operator|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|addedChildren
operator|.
name|add
argument_list|(
name|childName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addProperty
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|addedProps
operator|==
literal|null
condition|)
block|{
name|addedProps
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|addedProps
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getAddedChildren
parameter_list|()
block|{
return|return
name|addedChildren
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getAddedProps
parameter_list|()
block|{
return|return
name|addedProps
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|this
operator|.
name|get
argument_list|(
name|KEY_CHILDREN
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|childExists
parameter_list|(
name|String
name|childName
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|children
init|=
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
operator|&&
operator|!
name|children
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|children
operator|.
name|contains
argument_list|(
name|childName
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
name|addedChildExists
argument_list|(
name|childName
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|addedChildExists
parameter_list|(
name|String
name|childName
parameter_list|)
block|{
return|return
name|addedChildren
operator|!=
literal|null
operator|&&
operator|!
name|addedChildren
operator|.
name|isEmpty
argument_list|()
condition|?
name|addedChildren
operator|.
name|contains
argument_list|(
name|childName
argument_list|)
else|:
literal|false
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|PathUtils
operator|.
name|getName
argument_list|(
name|getString
argument_list|(
name|KEY_PATH
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|getString
argument_list|(
name|KEY_PATH
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getProperties
parameter_list|()
block|{
return|return
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|this
operator|.
name|get
argument_list|(
name|KEY_PROPERTIES
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getRemovedChildren
parameter_list|()
block|{
return|return
name|removedChildren
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getRemovedProps
parameter_list|()
block|{
return|return
name|removedProps
return|;
block|}
specifier|public
name|Long
name|getRevisionId
parameter_list|()
block|{
return|return
name|getLong
argument_list|(
name|KEY_REVISION_ID
argument_list|)
return|;
block|}
specifier|public
name|void
name|removeChild
parameter_list|(
name|String
name|childName
parameter_list|)
block|{
if|if
condition|(
name|removedChildren
operator|==
literal|null
condition|)
block|{
name|removedChildren
operator|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|removedChildren
operator|.
name|add
argument_list|(
name|childName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeProp
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|removedProps
operator|==
literal|null
condition|)
block|{
name|removedProps
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|removedProps
operator|.
name|put
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setBaseRevisionId
parameter_list|(
name|long
name|baseRevisionId
parameter_list|)
block|{
name|put
argument_list|(
name|KEY_BASE_REVISION_ID
argument_list|,
name|baseRevisionId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setChildren
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|children
parameter_list|)
block|{
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
name|put
argument_list|(
name|KEY_CHILDREN
argument_list|,
name|children
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|removeField
argument_list|(
name|KEY_CHILDREN
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|put
argument_list|(
name|KEY_PATH
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setProperties
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
block|{
if|if
condition|(
name|properties
operator|!=
literal|null
condition|)
block|{
name|put
argument_list|(
name|KEY_PROPERTIES
argument_list|,
name|properties
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|removeField
argument_list|(
name|KEY_PROPERTIES
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setRevisionId
parameter_list|(
name|long
name|revisionId
parameter_list|)
block|{
name|put
argument_list|(
name|KEY_REVISION_ID
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setRevisionId
parameter_list|(
name|String
name|revisionId
parameter_list|)
block|{
name|this
operator|.
name|setRevisionId
argument_list|(
name|MongoUtil
operator|.
name|toMongoRepresentation
argument_list|(
name|revisionId
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" internal props: "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"AddedChildren = "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|addedChildren
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", RemovedChildren = "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|removedChildren
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", AddedProps = "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|addedProps
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", RemovedProps = "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|removedProps
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

