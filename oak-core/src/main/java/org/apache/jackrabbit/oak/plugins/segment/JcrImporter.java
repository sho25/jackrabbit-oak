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
name|segment
package|;
end_package

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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NodeIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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
name|memory
operator|.
name|PropertyStates
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
name|NodeBuilder
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

begin_class
specifier|public
class|class
name|JcrImporter
block|{
specifier|private
specifier|final
name|SegmentWriter
name|writer
decl_stmt|;
specifier|public
name|JcrImporter
parameter_list|(
name|SegmentWriter
name|writer
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
block|}
specifier|public
name|NodeState
name|writeNode
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|buildNode
argument_list|(
name|builder
argument_list|,
name|node
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|void
name|buildNode
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|PropertyIterator
name|properties
init|=
name|node
operator|.
name|getProperties
argument_list|()
decl_stmt|;
while|while
condition|(
name|properties
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Property
name|property
init|=
name|properties
operator|.
name|nextProperty
argument_list|()
decl_stmt|;
if|if
condition|(
name|property
operator|.
name|isMultiple
argument_list|()
condition|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|property
operator|.
name|getValues
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|,
name|property
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|NodeIterator
name|childNodes
init|=
name|node
operator|.
name|getNodes
argument_list|()
decl_stmt|;
while|while
condition|(
name|childNodes
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Node
name|childNode
init|=
name|childNodes
operator|.
name|nextNode
argument_list|()
decl_stmt|;
name|buildNode
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|childNode
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|childNode
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

