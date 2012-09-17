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
name|DBCollection
import|;
end_import

begin_comment
comment|/**  * An query for fetching the current head.  *  * @author<a href="mailto:pmarx@adobe.com>Philipp Marx</a>  */
end_comment

begin_class
specifier|public
class|class
name|FetchHeadQuery
extends|extends
name|AbstractQuery
argument_list|<
name|HeadMongo
argument_list|>
block|{
comment|/**      * Constructs a new {@code FetchHeadQuery}.      *      * @param mongoConnection      *            The {@link MongoConnection}.      */
specifier|public
name|FetchHeadQuery
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
name|DBCollection
name|headCollection
init|=
name|mongoConnection
operator|.
name|getHeadCollection
argument_list|()
decl_stmt|;
name|HeadMongo
name|headMongo
init|=
operator|(
name|HeadMongo
operator|)
name|headCollection
operator|.
name|findOne
argument_list|()
decl_stmt|;
return|return
name|headMongo
return|;
block|}
block|}
end_class

end_unit

