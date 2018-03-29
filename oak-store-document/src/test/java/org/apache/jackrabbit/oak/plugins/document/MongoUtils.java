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
name|document
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
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|util
operator|.
name|MongoConnection
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
name|DB
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|MongoDatabase
import|;
end_import

begin_comment
comment|/**  * A utility class to get a {@link MongoConnection} to a local mongo instance  * and clean a test database.  */
end_comment

begin_class
specifier|public
class|class
name|MongoUtils
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|HOST
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"mongo.host"
argument_list|,
literal|"127.0.0.1"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|PORT
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"mongo.port"
argument_list|,
literal|27017
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DB
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"mongo.db"
argument_list|,
literal|"MongoMKDB"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|URL
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"mongo.url"
argument_list|,
literal|"mongodb://"
operator|+
name|HOST
operator|+
literal|":"
operator|+
name|PORT
operator|+
literal|"/"
operator|+
name|DB
operator|+
literal|"?connectTimeoutMS=3000&serverSelectionTimeoutMS=3000"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
name|Exception
name|exception
decl_stmt|;
comment|/**      * Get a connection if available. If not available, null is returned.      *      * @return the connection or null      */
specifier|public
specifier|static
name|MongoConnection
name|getConnection
parameter_list|()
block|{
return|return
name|getConnectionByURL
argument_list|(
name|URL
argument_list|)
return|;
block|}
comment|/**      * Get a connection if available. If not available, null is returned.      *      * @param dbName the database name      * @return the connection or null      */
specifier|public
specifier|static
name|MongoConnection
name|getConnection
parameter_list|(
name|String
name|dbName
parameter_list|)
block|{
return|return
name|getConnectionByURL
argument_list|(
literal|"mongodb://"
operator|+
name|HOST
operator|+
literal|":"
operator|+
name|PORT
operator|+
literal|"/"
operator|+
name|dbName
argument_list|)
return|;
block|}
comment|/**      * Drop all user defined collections in the given database. System      * collections are not dropped. This method returns silently if MongoDB is      * not available.      *      * @param dbName the database name.      */
specifier|public
specifier|static
name|void
name|dropCollections
parameter_list|(
name|String
name|dbName
parameter_list|)
block|{
name|MongoConnection
name|c
init|=
name|getConnection
argument_list|(
name|dbName
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
return|return;
block|}
try|try
block|{
name|dropCollections
argument_list|(
name|c
operator|.
name|getDB
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Drop all user defined collections. System collections are not dropped.      *      * @param db the connection      * @deprecated use {@link #dropCollections(MongoDatabase)} instead.      */
specifier|public
specifier|static
name|void
name|dropCollections
parameter_list|(
name|DB
name|db
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|db
operator|.
name|getCollectionNames
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|name
operator|.
name|startsWith
argument_list|(
literal|"system."
argument_list|)
condition|)
block|{
name|db
operator|.
name|getCollection
argument_list|(
name|name
argument_list|)
operator|.
name|drop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Drop all user defined collections. System collections are not dropped.      *      * @param db the connection      */
specifier|public
specifier|static
name|void
name|dropCollections
parameter_list|(
name|MongoDatabase
name|db
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|db
operator|.
name|listCollectionNames
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|name
operator|.
name|startsWith
argument_list|(
literal|"system."
argument_list|)
condition|)
block|{
name|db
operator|.
name|getCollection
argument_list|(
name|name
argument_list|)
operator|.
name|drop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Drops the database with the given name. This method returns silently if      * MongoDB is not available.      *      * @param dbName the name of the database to drop.      */
specifier|public
specifier|static
name|void
name|dropDatabase
parameter_list|(
name|String
name|dbName
parameter_list|)
block|{
name|MongoConnection
name|c
init|=
name|getConnection
argument_list|(
name|dbName
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
return|return;
block|}
try|try
block|{
name|c
operator|.
name|getDB
argument_list|()
operator|.
name|dropDatabase
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return true if MongoDB is available, false otherwise.      */
specifier|public
specifier|static
name|boolean
name|isAvailable
parameter_list|()
block|{
name|MongoConnection
name|c
init|=
name|getConnection
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|c
operator|!=
literal|null
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|//----------------------------< internal>----------------------------------
comment|/**      * Get a connection if available. If not available, null is returned.      *      * @param url the mongodb url      * @return the connection or null      */
specifier|private
specifier|static
name|MongoConnection
name|getConnectionByURL
parameter_list|(
name|String
name|url
parameter_list|)
block|{
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|MongoConnection
name|mongoConnection
decl_stmt|;
try|try
block|{
name|mongoConnection
operator|=
operator|new
name|MongoConnection
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|mongoConnection
operator|.
name|getDB
argument_list|()
operator|.
name|command
argument_list|(
operator|new
name|BasicDBObject
argument_list|(
literal|"ping"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// dropCollections(mongoConnection.getDB());
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
name|mongoConnection
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|mongoConnection
return|;
block|}
block|}
end_class

end_unit

