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
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Objects
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
name|MongoClient
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoClientOptions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoClientURI
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * The {@code MongoConnection} abstracts connection to the {@code MongoDB}.  */
end_comment

begin_class
specifier|public
class|class
name|MongoConnection
block|{
specifier|private
specifier|static
specifier|final
name|WriteConcern
name|WC_UNKNOWN
init|=
operator|new
name|WriteConcern
argument_list|(
literal|"unknown"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|MongoClientURI
name|mongoURI
decl_stmt|;
specifier|private
specifier|final
name|MongoClient
name|mongo
decl_stmt|;
comment|/**      * Constructs a new connection using the specified MongoDB connection string.      * See also http://docs.mongodb.org/manual/reference/connection-string/      *      * @param uri the MongoDB URI      * @throws UnknownHostException      */
specifier|public
name|MongoConnection
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|UnknownHostException
block|{
name|MongoClientOptions
operator|.
name|Builder
name|builder
init|=
name|MongoConnection
operator|.
name|getDefaultBuilder
argument_list|()
decl_stmt|;
name|mongoURI
operator|=
operator|new
name|MongoClientURI
argument_list|(
name|uri
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|mongo
operator|=
operator|new
name|MongoClient
argument_list|(
name|mongoURI
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new {@code MongoConnection}.      *      * @param host The host address.      * @param port The port.      * @param database The database name.      * @throws Exception If an error occurred while trying to connect.      */
specifier|public
name|MongoConnection
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|database
parameter_list|)
throws|throws
name|Exception
block|{
name|this
argument_list|(
literal|"mongodb://"
operator|+
name|host
operator|+
literal|":"
operator|+
name|port
operator|+
literal|"/"
operator|+
name|database
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the {@link DB} as passed in the URI of the constructor.      *      * @return The {@link DB}.      */
specifier|public
name|DB
name|getDB
parameter_list|()
block|{
return|return
name|mongo
operator|.
name|getDB
argument_list|(
name|mongoURI
operator|.
name|getDatabase
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns the {@link DB} with the given name.      *      * @return The {@link DB}.      */
specifier|public
name|DB
name|getDB
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
name|mongo
operator|.
name|getDB
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Closes the underlying Mongo instance      */
specifier|public
name|void
name|close
parameter_list|()
block|{
name|mongo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//--------------------------------------< Utility Methods>
comment|/**      * Constructs a builder with default options set. These can be overridden later      *      * @return builder with default options set      */
specifier|public
specifier|static
name|MongoClientOptions
operator|.
name|Builder
name|getDefaultBuilder
parameter_list|()
block|{
return|return
operator|new
name|MongoClientOptions
operator|.
name|Builder
argument_list|()
operator|.
name|description
argument_list|(
literal|"MongoConnection for Oak DocumentMK"
argument_list|)
operator|.
name|threadsAllowedToBlockForConnectionMultiplier
argument_list|(
literal|100
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|MongoClientOptions
name|opts
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|toStringHelper
argument_list|(
name|opts
argument_list|)
operator|.
name|add
argument_list|(
literal|"connectionsPerHost"
argument_list|,
name|opts
operator|.
name|getConnectionsPerHost
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"connectTimeout"
argument_list|,
name|opts
operator|.
name|getConnectTimeout
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"socketTimeout"
argument_list|,
name|opts
operator|.
name|getSocketTimeout
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"socketKeepAlive"
argument_list|,
name|opts
operator|.
name|isSocketKeepAlive
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"maxWaitTime"
argument_list|,
name|opts
operator|.
name|getMaxWaitTime
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"threadsAllowedToBlockForConnectionMultiplier"
argument_list|,
name|opts
operator|.
name|getThreadsAllowedToBlockForConnectionMultiplier
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"readPreference"
argument_list|,
name|opts
operator|.
name|getReadPreference
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"writeConcern"
argument_list|,
name|opts
operator|.
name|getWriteConcern
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Returns {@code true} if the given {@code uri} has a write concern set.      * @param uri the URI to check.      * @return {@code true} if the URI has a write concern set, {@code false}      *      otherwise.      */
specifier|public
specifier|static
name|boolean
name|hasWriteConcern
parameter_list|(
annotation|@
name|Nonnull
name|String
name|uri
parameter_list|)
block|{
name|MongoClientOptions
operator|.
name|Builder
name|builder
init|=
name|MongoClientOptions
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|writeConcern
argument_list|(
name|WC_UNKNOWN
argument_list|)
expr_stmt|;
name|WriteConcern
name|wc
init|=
operator|new
name|MongoClientURI
argument_list|(
name|checkNotNull
argument_list|(
name|uri
argument_list|)
argument_list|,
name|builder
argument_list|)
operator|.
name|getOptions
argument_list|()
operator|.
name|getWriteConcern
argument_list|()
decl_stmt|;
return|return
operator|!
name|WC_UNKNOWN
operator|.
name|equals
argument_list|(
name|wc
argument_list|)
return|;
block|}
comment|/**      * Returns the default write concern depending on MongoDB deployment.      *<ul>      *<li>{@link WriteConcern#MAJORITY}: for a MongoDB replica set</li>      *<li>{@link WriteConcern#ACKNOWLEDGED}: for single MongoDB instance</li>      *</ul>      *      * @param db the connection to MongoDB.      * @return the default write concern to use for Oak.      */
specifier|public
specifier|static
name|WriteConcern
name|getDefaultWriteConcern
parameter_list|(
annotation|@
name|Nonnull
name|DB
name|db
parameter_list|)
block|{
name|WriteConcern
name|w
decl_stmt|;
if|if
condition|(
name|checkNotNull
argument_list|(
name|db
argument_list|)
operator|.
name|getMongo
argument_list|()
operator|.
name|getReplicaSetStatus
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|w
operator|=
name|WriteConcern
operator|.
name|MAJORITY
expr_stmt|;
block|}
else|else
block|{
name|w
operator|=
name|WriteConcern
operator|.
name|ACKNOWLEDGED
expr_stmt|;
block|}
return|return
name|w
return|;
block|}
comment|/**      * Returns {@code true} if the default write concern on the {@code db} is      * sufficient for Oak. On a replica set Oak expects at least w=2. For      * a single MongoDB node deployment w=1 is sufficient.      *      * @param db the database.      * @return whether the write concern is sufficient.      */
specifier|public
specifier|static
name|boolean
name|hasSufficientWriteConcern
parameter_list|(
annotation|@
name|Nonnull
name|DB
name|db
parameter_list|)
block|{
name|Object
name|wObj
init|=
name|checkNotNull
argument_list|(
name|db
argument_list|)
operator|.
name|getWriteConcern
argument_list|()
operator|.
name|getWObject
argument_list|()
decl_stmt|;
name|int
name|w
decl_stmt|;
if|if
condition|(
name|wObj
operator|instanceof
name|Number
condition|)
block|{
name|w
operator|=
operator|(
operator|(
name|Number
operator|)
name|wObj
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|WriteConcern
operator|.
name|MAJORITY
operator|.
name|getWString
argument_list|()
operator|.
name|equals
argument_list|(
name|wObj
argument_list|)
condition|)
block|{
comment|// majority in a replica set means at least w=2
name|w
operator|=
literal|2
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown write concern: "
operator|+
name|db
operator|.
name|getWriteConcern
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|db
operator|.
name|getMongo
argument_list|()
operator|.
name|getReplicaSetStatus
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|w
operator|>=
literal|2
return|;
block|}
else|else
block|{
return|return
name|w
operator|>=
literal|1
return|;
block|}
block|}
block|}
end_class

end_unit

