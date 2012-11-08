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
name|command
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
name|mongomk
operator|.
name|impl
operator|.
name|MongoNodeStore
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
name|action
operator|.
name|FetchHeadRevisionIdAction
import|;
end_import

begin_comment
comment|/**  * {@code Command} for {@code MongoMicroKernel#getHeadRevision()}  */
end_comment

begin_class
specifier|public
class|class
name|GetHeadRevisionCommand
extends|extends
name|BaseCommand
argument_list|<
name|Long
argument_list|>
block|{
comment|/**      * Constructs a new {@code GetHeadRevisionCommandMongo}.      *      * @param nodeStore Node store.      */
specifier|public
name|GetHeadRevisionCommand
parameter_list|(
name|MongoNodeStore
name|nodeStore
parameter_list|)
block|{
name|super
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Long
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|FetchHeadRevisionIdAction
argument_list|(
name|nodeStore
argument_list|)
operator|.
name|execute
argument_list|()
return|;
block|}
block|}
end_class

end_unit

