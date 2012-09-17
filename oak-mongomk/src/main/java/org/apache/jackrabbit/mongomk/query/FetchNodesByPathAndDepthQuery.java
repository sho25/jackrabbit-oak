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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|log4j
operator|.
name|Logger
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
name|DBCursor
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
name|QueryBuilder
import|;
end_import

begin_comment
comment|/**  * An query for fetching nodes by path and depth.  *  * @author<a href="mailto:pmarx@adobe.com>Philipp Marx</a>  */
end_comment

begin_class
specifier|public
class|class
name|FetchNodesByPathAndDepthQuery
extends|extends
name|AbstractQuery
argument_list|<
name|List
argument_list|<
name|NodeMongo
argument_list|>
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|FetchNodesByPathAndDepthQuery
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|int
name|depth
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|String
name|revisionId
decl_stmt|;
comment|/**      * Constructs a new {@code FetchNodesByPathAndDepthQuery}.      *      * @param mongoConnection      *            The {@link MongoConnection}.      * @param path      *            The path.      * @param revisionId      *            The revision id.      * @param depth      *            The depth.      */
specifier|public
name|FetchNodesByPathAndDepthQuery
parameter_list|(
name|MongoConnection
name|mongoConnection
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|revisionId
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
name|super
argument_list|(
name|mongoConnection
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|revisionId
operator|=
name|revisionId
expr_stmt|;
name|this
operator|.
name|depth
operator|=
name|depth
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|NodeMongo
argument_list|>
name|execute
parameter_list|()
block|{
name|Pattern
name|pattern
init|=
name|createPrefixRegExp
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|validRevisions
init|=
name|fetchValidRevisions
argument_list|(
name|mongoConnection
argument_list|,
name|revisionId
argument_list|)
decl_stmt|;
name|DBCursor
name|dbCursor
init|=
name|performQuery
argument_list|(
name|pattern
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|NodeMongo
argument_list|>
name|nodes
init|=
name|QueryUtils
operator|.
name|convertToNodes
argument_list|(
name|dbCursor
argument_list|,
name|validRevisions
argument_list|)
decl_stmt|;
return|return
name|nodes
return|;
block|}
specifier|private
name|Pattern
name|createPrefixRegExp
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|depth
operator|<
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"^"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|depth
operator|==
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"^"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"$"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|depth
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"^"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
literal|"/"
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"(/[^/]*)"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"{0,"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|depth
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"}$"
argument_list|)
expr_stmt|;
block|}
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|pattern
return|;
block|}
specifier|private
name|List
argument_list|<
name|Long
argument_list|>
name|fetchValidRevisions
parameter_list|(
name|MongoConnection
name|mongoConnection
parameter_list|,
name|String
name|revisionId
parameter_list|)
block|{
return|return
operator|new
name|FetchValidRevisionsQuery
argument_list|(
name|mongoConnection
argument_list|,
name|revisionId
argument_list|)
operator|.
name|execute
argument_list|()
return|;
block|}
specifier|private
name|DBCursor
name|performQuery
parameter_list|(
name|Pattern
name|pattern
parameter_list|)
block|{
name|DBCollection
name|nodeCollection
init|=
name|mongoConnection
operator|.
name|getNodeCollection
argument_list|()
decl_stmt|;
name|QueryBuilder
name|qb
init|=
name|QueryBuilder
operator|.
name|start
argument_list|(
name|NodeMongo
operator|.
name|KEY_PATH
argument_list|)
operator|.
name|regex
argument_list|(
name|pattern
argument_list|)
decl_stmt|;
if|if
condition|(
name|revisionId
operator|!=
literal|null
condition|)
block|{
name|qb
operator|=
name|qb
operator|.
name|and
argument_list|(
name|NodeMongo
operator|.
name|KEY_REVISION_ID
argument_list|)
operator|.
name|lessThanEquals
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
name|DBObject
name|query
init|=
name|qb
operator|.
name|get
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Executing query: %s"
argument_list|,
name|query
argument_list|)
argument_list|)
expr_stmt|;
name|DBCursor
name|dbCursor
init|=
name|nodeCollection
operator|.
name|find
argument_list|(
name|query
argument_list|)
decl_stmt|;
return|return
name|dbCursor
return|;
block|}
block|}
end_class

end_unit

