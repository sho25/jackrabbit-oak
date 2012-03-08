begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|store
operator|.
name|persistence
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|blobs
operator|.
name|BlobStore
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
name|fs
operator|.
name|FilePath
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
name|model
operator|.
name|ChildNodeEntriesMap
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
name|model
operator|.
name|Commit
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
name|model
operator|.
name|Id
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
name|mk
operator|.
name|model
operator|.
name|StoredCommit
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
name|store
operator|.
name|BinaryBinding
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
name|store
operator|.
name|Binding
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
name|store
operator|.
name|IdFactory
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
name|store
operator|.
name|NotFoundException
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
name|util
operator|.
name|ExceptionFactory
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
name|util
operator|.
name|IOUtils
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|types
operator|.
name|ObjectId
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
name|Mongo
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoException
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
name|gridfs
operator|.
name|GridFS
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|gridfs
operator|.
name|GridFSDBFile
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|gridfs
operator|.
name|GridFSInputFile
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|MongoPersistence
implements|implements
name|Persistence
implements|,
name|BlobStore
block|{
specifier|private
specifier|static
specifier|final
name|boolean
name|BINARY_FORMAT
init|=
literal|false
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|HEAD_COLLECTION
init|=
literal|"head"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NODES_COLLECTION
init|=
literal|"nodes"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|COMMITS_COLLECTION
init|=
literal|"commits"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CNEMAPS_COLLECTION
init|=
literal|"cneMaps"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ID_FIELD
init|=
literal|":id"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DATA_FIELD
init|=
literal|":data"
decl_stmt|;
specifier|private
name|Mongo
name|con
decl_stmt|;
specifier|private
name|DB
name|db
decl_stmt|;
specifier|private
name|DBCollection
name|nodes
decl_stmt|;
specifier|private
name|DBCollection
name|commits
decl_stmt|;
specifier|private
name|DBCollection
name|cneMaps
decl_stmt|;
specifier|private
name|GridFS
name|fs
decl_stmt|;
comment|// TODO: make this configurable
specifier|private
name|IdFactory
name|idFactory
init|=
name|IdFactory
operator|.
name|getDigestFactory
argument_list|()
decl_stmt|;
specifier|public
name|void
name|initialize
parameter_list|(
name|File
name|homeDir
parameter_list|)
throws|throws
name|Exception
block|{
name|con
operator|=
operator|new
name|Mongo
argument_list|()
expr_stmt|;
comment|//con = new Mongo("localhost", 27017);
name|db
operator|=
name|con
operator|.
name|getDB
argument_list|(
literal|"mk"
argument_list|)
expr_stmt|;
name|db
operator|.
name|setWriteConcern
argument_list|(
name|WriteConcern
operator|.
name|SAFE
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|db
operator|.
name|collectionExists
argument_list|(
name|HEAD_COLLECTION
argument_list|)
condition|)
block|{
comment|// capped collection of size 1
name|db
operator|.
name|createCollection
argument_list|(
name|HEAD_COLLECTION
argument_list|,
operator|new
name|BasicDBObject
argument_list|(
literal|"capped"
argument_list|,
literal|true
argument_list|)
operator|.
name|append
argument_list|(
literal|"size"
argument_list|,
literal|256
argument_list|)
operator|.
name|append
argument_list|(
literal|"max"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|nodes
operator|=
name|db
operator|.
name|getCollection
argument_list|(
name|NODES_COLLECTION
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|ensureIndex
argument_list|(
operator|new
name|BasicDBObject
argument_list|(
name|ID_FIELD
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|BasicDBObject
argument_list|(
literal|"unique"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|commits
operator|=
name|db
operator|.
name|getCollection
argument_list|(
name|COMMITS_COLLECTION
argument_list|)
expr_stmt|;
name|commits
operator|.
name|ensureIndex
argument_list|(
operator|new
name|BasicDBObject
argument_list|(
name|ID_FIELD
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|BasicDBObject
argument_list|(
literal|"unique"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|cneMaps
operator|=
name|db
operator|.
name|getCollection
argument_list|(
name|CNEMAPS_COLLECTION
argument_list|)
expr_stmt|;
name|cneMaps
operator|.
name|ensureIndex
argument_list|(
operator|new
name|BasicDBObject
argument_list|(
name|ID_FIELD
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|BasicDBObject
argument_list|(
literal|"unique"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|=
operator|new
name|GridFS
argument_list|(
name|db
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|=
literal|null
expr_stmt|;
name|db
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|Id
name|readHead
parameter_list|()
throws|throws
name|Exception
block|{
name|DBObject
name|entry
init|=
name|db
operator|.
name|getCollection
argument_list|(
name|HEAD_COLLECTION
argument_list|)
operator|.
name|findOne
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|Id
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|entry
operator|.
name|get
argument_list|(
name|ID_FIELD
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|writeHead
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|Exception
block|{
comment|// capped collection of size 1
name|db
operator|.
name|getCollection
argument_list|(
name|HEAD_COLLECTION
argument_list|)
operator|.
name|insert
argument_list|(
operator|new
name|BasicDBObject
argument_list|(
name|ID_FIELD
argument_list|,
name|id
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Binding
name|readNodeBinding
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|BasicDBObject
name|key
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
if|if
condition|(
name|BINARY_FORMAT
condition|)
block|{
name|key
operator|.
name|put
argument_list|(
name|ID_FIELD
argument_list|,
name|id
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|key
operator|.
name|put
argument_list|(
name|ID_FIELD
argument_list|,
name|id
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BasicDBObject
name|nodeObject
init|=
operator|(
name|BasicDBObject
operator|)
name|nodes
operator|.
name|findOne
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeObject
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|BINARY_FORMAT
condition|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|(
name|byte
index|[]
operator|)
name|nodeObject
operator|.
name|get
argument_list|(
name|DATA_FIELD
argument_list|)
decl_stmt|;
return|return
operator|new
name|BinaryBinding
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|DBObjectBinding
argument_list|(
name|nodeObject
argument_list|)
return|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Id
name|writeNode
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|node
operator|.
name|serialize
argument_list|(
operator|new
name|BinaryBinding
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|Id
name|id
init|=
operator|new
name|Id
argument_list|(
name|idFactory
operator|.
name|createContentId
argument_list|(
name|bytes
argument_list|)
argument_list|)
decl_stmt|;
name|BasicDBObject
name|nodeObject
decl_stmt|;
if|if
condition|(
name|BINARY_FORMAT
condition|)
block|{
name|nodeObject
operator|=
operator|new
name|BasicDBObject
argument_list|(
name|ID_FIELD
argument_list|,
name|id
operator|.
name|getBytes
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|DATA_FIELD
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nodeObject
operator|=
operator|new
name|BasicDBObject
argument_list|(
name|ID_FIELD
argument_list|,
name|id
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|serialize
argument_list|(
operator|new
name|DBObjectBinding
argument_list|(
name|nodeObject
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|nodes
operator|.
name|insert
argument_list|(
name|nodeObject
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MongoException
operator|.
name|DuplicateKey
name|ignore
parameter_list|)
block|{
comment|// fall through
block|}
return|return
name|id
return|;
block|}
specifier|public
name|StoredCommit
name|readCommit
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|BasicDBObject
name|key
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
if|if
condition|(
name|BINARY_FORMAT
condition|)
block|{
name|key
operator|.
name|put
argument_list|(
name|ID_FIELD
argument_list|,
name|id
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|key
operator|.
name|put
argument_list|(
name|ID_FIELD
argument_list|,
name|id
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|BasicDBObject
name|commitObject
init|=
operator|(
name|BasicDBObject
operator|)
name|commits
operator|.
name|findOne
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|commitObject
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|BINARY_FORMAT
condition|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|(
name|byte
index|[]
operator|)
name|commitObject
operator|.
name|get
argument_list|(
name|DATA_FIELD
argument_list|)
decl_stmt|;
return|return
name|StoredCommit
operator|.
name|deserialize
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|BinaryBinding
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|StoredCommit
operator|.
name|deserialize
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|DBObjectBinding
argument_list|(
name|commitObject
argument_list|)
argument_list|)
return|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|writeCommit
parameter_list|(
name|Id
name|id
parameter_list|,
name|Commit
name|commit
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|commit
operator|.
name|serialize
argument_list|(
operator|new
name|BinaryBinding
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|BasicDBObject
name|commitObject
decl_stmt|;
if|if
condition|(
name|BINARY_FORMAT
condition|)
block|{
name|commitObject
operator|=
operator|new
name|BasicDBObject
argument_list|(
name|ID_FIELD
argument_list|,
name|id
operator|.
name|getBytes
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|DATA_FIELD
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|commitObject
operator|=
operator|new
name|BasicDBObject
argument_list|(
name|ID_FIELD
argument_list|,
name|id
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|commit
operator|.
name|serialize
argument_list|(
operator|new
name|DBObjectBinding
argument_list|(
name|commitObject
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|commits
operator|.
name|insert
argument_list|(
name|commitObject
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MongoException
operator|.
name|DuplicateKey
name|ignore
parameter_list|)
block|{
comment|// fall through
block|}
block|}
specifier|public
name|ChildNodeEntriesMap
name|readCNEMap
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|BasicDBObject
name|key
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
if|if
condition|(
name|BINARY_FORMAT
condition|)
block|{
name|key
operator|.
name|put
argument_list|(
name|ID_FIELD
argument_list|,
name|id
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|key
operator|.
name|put
argument_list|(
name|ID_FIELD
argument_list|,
name|id
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|BasicDBObject
name|mapObject
init|=
operator|(
name|BasicDBObject
operator|)
name|cneMaps
operator|.
name|findOne
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapObject
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|BINARY_FORMAT
condition|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|(
name|byte
index|[]
operator|)
name|mapObject
operator|.
name|get
argument_list|(
name|DATA_FIELD
argument_list|)
decl_stmt|;
return|return
name|ChildNodeEntriesMap
operator|.
name|deserialize
argument_list|(
operator|new
name|BinaryBinding
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ChildNodeEntriesMap
operator|.
name|deserialize
argument_list|(
operator|new
name|DBObjectBinding
argument_list|(
name|mapObject
argument_list|)
argument_list|)
return|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Id
name|writeCNEMap
parameter_list|(
name|ChildNodeEntriesMap
name|map
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|map
operator|.
name|serialize
argument_list|(
operator|new
name|BinaryBinding
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|Id
name|id
init|=
operator|new
name|Id
argument_list|(
name|idFactory
operator|.
name|createContentId
argument_list|(
name|bytes
argument_list|)
argument_list|)
decl_stmt|;
name|BasicDBObject
name|mapObject
decl_stmt|;
if|if
condition|(
name|BINARY_FORMAT
condition|)
block|{
name|mapObject
operator|=
operator|new
name|BasicDBObject
argument_list|(
name|ID_FIELD
argument_list|,
name|id
operator|.
name|getBytes
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|DATA_FIELD
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mapObject
operator|=
operator|new
name|BasicDBObject
argument_list|(
name|ID_FIELD
argument_list|,
name|id
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|serialize
argument_list|(
operator|new
name|DBObjectBinding
argument_list|(
name|mapObject
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|cneMaps
operator|.
name|insert
argument_list|(
name|mapObject
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MongoException
operator|.
name|DuplicateKey
name|ignore
parameter_list|)
block|{
comment|// fall through
block|}
return|return
name|id
return|;
block|}
comment|//------------------------------------------------------------< BlobStore>
specifier|public
name|String
name|addBlob
parameter_list|(
name|String
name|tempFilePath
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|FilePath
name|file
init|=
name|FilePath
operator|.
name|get
argument_list|(
name|tempFilePath
argument_list|)
decl_stmt|;
try|try
block|{
name|InputStream
name|in
init|=
name|file
operator|.
name|newInputStream
argument_list|()
decl_stmt|;
return|return
name|writeBlob
argument_list|(
name|in
argument_list|)
return|;
block|}
finally|finally
block|{
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|ExceptionFactory
operator|.
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|String
name|writeBlob
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|Exception
block|{
name|GridFSInputFile
name|f
init|=
name|fs
operator|.
name|createFile
argument_list|(
name|in
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|//f.save(0x20000);   // save in 128k chunks
name|f
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
name|f
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|int
name|readBlob
parameter_list|(
name|String
name|blobId
parameter_list|,
name|long
name|pos
parameter_list|,
name|byte
index|[]
name|buff
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|Exception
block|{
name|GridFSDBFile
name|f
init|=
name|fs
operator|.
name|findOne
argument_list|(
operator|new
name|ObjectId
argument_list|(
name|blobId
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|blobId
argument_list|)
throw|;
block|}
comment|// todo provide a more efficient implementation
name|InputStream
name|in
init|=
name|f
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|in
operator|.
name|skip
argument_list|(
name|pos
argument_list|)
expr_stmt|;
return|return
name|in
operator|.
name|read
argument_list|(
name|buff
argument_list|,
name|off
argument_list|,
name|length
argument_list|)
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|long
name|getBlobLength
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|Exception
block|{
name|GridFSDBFile
name|f
init|=
name|fs
operator|.
name|findOne
argument_list|(
operator|new
name|ObjectId
argument_list|(
name|blobId
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|blobId
argument_list|)
throw|;
block|}
return|return
name|f
operator|.
name|getLength
argument_list|()
return|;
block|}
comment|//-------------------------------------------------------< implementation>
specifier|protected
specifier|final
specifier|static
name|String
name|ENCODED_DOT
init|=
literal|"_x46_"
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|ENCODED_DOLLAR_SIGN
init|=
literal|"_x36_"
decl_stmt|;
comment|/**      * see http://www.mongodb.org/display/DOCS/Legal+Key+Names      *      * @param name      * @return      */
specifier|protected
specifier|static
name|String
name|encodeName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|name
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|==
literal|0
operator|&&
name|name
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'$'
condition|)
block|{
comment|// mongodb field names must not start with '$'
name|buf
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|ENCODED_DOLLAR_SIGN
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'.'
condition|)
block|{
comment|// . is a reserved char for mongodb field names
if|if
condition|(
name|buf
operator|==
literal|null
condition|)
block|{
name|buf
operator|=
operator|new
name|StringBuilder
argument_list|(
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|ENCODED_DOT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|buf
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|name
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|buf
operator|==
literal|null
condition|?
name|name
else|:
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
specifier|static
name|String
name|decodeName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
literal|null
decl_stmt|;
name|int
name|lastPos
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|ENCODED_DOLLAR_SIGN
argument_list|)
condition|)
block|{
name|buf
operator|=
operator|new
name|StringBuilder
argument_list|(
literal|"$"
argument_list|)
expr_stmt|;
name|lastPos
operator|=
name|ENCODED_DOLLAR_SIGN
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|int
name|pos
decl_stmt|;
while|while
condition|(
operator|(
name|pos
operator|=
name|name
operator|.
name|indexOf
argument_list|(
name|ENCODED_DOT
argument_list|,
name|lastPos
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|buf
operator|==
literal|null
condition|)
block|{
name|buf
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|lastPos
argument_list|,
name|pos
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
name|lastPos
operator|=
name|pos
operator|+
name|ENCODED_DOT
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|buf
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|lastPos
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|name
return|;
block|}
block|}
comment|//--------------------------------------------------------< inner classes>
specifier|protected
class|class
name|DBObjectBinding
implements|implements
name|Binding
block|{
name|BasicDBObject
name|obj
decl_stmt|;
specifier|protected
name|DBObjectBinding
parameter_list|(
name|BasicDBObject
name|obj
parameter_list|)
block|{
name|this
operator|.
name|obj
operator|=
name|obj
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|Exception
block|{
name|obj
operator|.
name|append
argument_list|(
name|encodeName
argument_list|(
name|key
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|String
name|key
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
throws|throws
name|Exception
block|{
name|obj
operator|.
name|append
argument_list|(
name|encodeName
argument_list|(
name|key
argument_list|)
argument_list|,
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|value
parameter_list|)
throws|throws
name|Exception
block|{
name|obj
operator|.
name|append
argument_list|(
name|encodeName
argument_list|(
name|key
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|String
name|key
parameter_list|,
name|int
name|value
parameter_list|)
throws|throws
name|Exception
block|{
name|obj
operator|.
name|append
argument_list|(
name|encodeName
argument_list|(
name|key
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeMap
parameter_list|(
name|String
name|key
parameter_list|,
name|int
name|count
parameter_list|,
name|StringEntryIterator
name|iterator
parameter_list|)
throws|throws
name|Exception
block|{
name|BasicDBObject
name|childObj
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|StringEntry
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|childObj
operator|.
name|append
argument_list|(
name|encodeName
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|obj
operator|.
name|append
argument_list|(
name|encodeName
argument_list|(
name|key
argument_list|)
argument_list|,
name|childObj
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeMap
parameter_list|(
name|String
name|key
parameter_list|,
name|int
name|count
parameter_list|,
name|BytesEntryIterator
name|iterator
parameter_list|)
throws|throws
name|Exception
block|{
name|BasicDBObject
name|childObj
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|BytesEntry
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|childObj
operator|.
name|append
argument_list|(
name|encodeName
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|obj
operator|.
name|append
argument_list|(
name|encodeName
argument_list|(
name|key
argument_list|)
argument_list|,
name|childObj
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|readStringValue
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|obj
operator|.
name|getString
argument_list|(
name|encodeName
argument_list|(
name|key
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|readBytesValue
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|StringUtils
operator|.
name|convertHexToBytes
argument_list|(
name|obj
operator|.
name|getString
argument_list|(
name|encodeName
argument_list|(
name|key
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|readLongValue
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|obj
operator|.
name|getLong
argument_list|(
name|encodeName
argument_list|(
name|key
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|readIntValue
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|obj
operator|.
name|getInt
argument_list|(
name|encodeName
argument_list|(
name|key
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|StringEntryIterator
name|readStringMap
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|BasicDBObject
name|childObj
init|=
operator|(
name|BasicDBObject
operator|)
name|obj
operator|.
name|get
argument_list|(
name|encodeName
argument_list|(
name|key
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|childObj
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|StringEntryIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|it
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|StringEntry
name|next
parameter_list|()
block|{
name|String
name|key
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|new
name|StringEntry
argument_list|(
name|decodeName
argument_list|(
name|key
argument_list|)
argument_list|,
name|childObj
operator|.
name|getString
argument_list|(
name|key
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesEntryIterator
name|readBytesMap
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|BasicDBObject
name|childObj
init|=
operator|(
name|BasicDBObject
operator|)
name|obj
operator|.
name|get
argument_list|(
name|encodeName
argument_list|(
name|key
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|childObj
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|BytesEntryIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|it
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesEntry
name|next
parameter_list|()
block|{
name|String
name|key
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|new
name|BytesEntry
argument_list|(
name|decodeName
argument_list|(
name|key
argument_list|)
argument_list|,
name|StringUtils
operator|.
name|convertHexToBytes
argument_list|(
name|childObj
operator|.
name|getString
argument_list|(
name|key
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
block|}
end_class

end_unit

