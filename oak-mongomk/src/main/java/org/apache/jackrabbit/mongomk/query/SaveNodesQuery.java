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
name|query
package|;
end_package

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
name|MongoConnection
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
name|model
operator|.
name|NodeMongo
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBCollection
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

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|WriteConcern
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|WriteResult
import|;
end_import

begin_comment
comment|/**  * An query for saving a list of nodes.  *  * @author<a href="mailto:pmarx@adobe.com>Philipp Marx</a>  */
end_comment

begin_class
specifier|public
class|class
name|SaveNodesQuery
extends|extends
name|AbstractQuery
argument_list|<
name|Boolean
argument_list|>
block|{
specifier|private
specifier|final
name|Collection
argument_list|<
name|NodeMongo
argument_list|>
name|nodeMongos
decl_stmt|;
comment|/**      * Constructs a new {@code SaveNodesQuery}.      *      * @param mongoConnection      *            The {@link MongoConnection}.      * @param nodeMongos      *            The list of {@link NodeMongo}s.      */
specifier|public
name|SaveNodesQuery
parameter_list|(
name|MongoConnection
name|mongoConnection
parameter_list|,
name|Collection
argument_list|<
name|NodeMongo
argument_list|>
name|nodeMongos
parameter_list|)
block|{
name|super
argument_list|(
name|mongoConnection
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeMongos
operator|=
name|nodeMongos
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Boolean
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|DBCollection
name|nodeCollection
init|=
name|mongoConnection
operator|.
name|getNodeCollection
argument_list|()
decl_stmt|;
name|DBObject
index|[]
name|temp
init|=
name|nodeMongos
operator|.
name|toArray
argument_list|(
operator|new
name|DBObject
index|[
name|nodeMongos
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|WriteResult
name|writeResult
init|=
name|nodeCollection
operator|.
name|insert
argument_list|(
name|temp
argument_list|,
name|WriteConcern
operator|.
name|SAFE
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|writeResult
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|writeResult
operator|.
name|getError
argument_list|()
operator|!=
literal|null
operator|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Insertion wasn't successful: %s"
argument_list|,
name|writeResult
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|Boolean
operator|.
name|TRUE
return|;
block|}
block|}
end_class

end_unit

