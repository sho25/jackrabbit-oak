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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
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
name|HeadMongo
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

begin_comment
comment|/**  * An query for reading and incrementing the head revisio id.  *  * @author<a href="mailto:pmarx@adobe.com>Philipp Marx</a>  */
end_comment

begin_class
specifier|public
class|class
name|ReadAndIncHeadRevisionQuery
extends|extends
name|AbstractQuery
argument_list|<
name|HeadMongo
argument_list|>
block|{
comment|/**      * Constructs a new {@code ReadAndIncHeadRevisionQuery}.      *      * @param mongoConnection      *            The {@link MongoConnection}.      */
specifier|public
name|ReadAndIncHeadRevisionQuery
parameter_list|(
name|MongoConnection
name|mongoConnection
parameter_list|)
block|{
name|super
argument_list|(
name|mongoConnection
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|HeadMongo
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|DBObject
name|query
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|DBObject
name|inc
init|=
operator|new
name|BasicDBObject
argument_list|(
name|HeadMongo
operator|.
name|KEY_NEXT_REVISION_ID
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|DBObject
name|update
init|=
operator|new
name|BasicDBObject
argument_list|(
literal|"$inc"
argument_list|,
name|inc
argument_list|)
decl_stmt|;
name|DBCollection
name|headCollection
init|=
name|mongoConnection
operator|.
name|getHeadCollection
argument_list|()
decl_stmt|;
name|DBObject
name|dbObject
init|=
name|headCollection
operator|.
name|findAndModify
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|update
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Not sure why but sometimes dbObject is null. Simply retry for now.
while|while
condition|(
name|dbObject
operator|==
literal|null
condition|)
block|{
name|dbObject
operator|=
name|headCollection
operator|.
name|findAndModify
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|update
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|HeadMongo
operator|.
name|fromDBObject
argument_list|(
name|dbObject
argument_list|)
return|;
block|}
block|}
end_class

end_unit

