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
name|command
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|digest
operator|.
name|DigestUtils
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
name|command
operator|.
name|AbstractCommand
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

begin_class
specifier|public
class|class
name|WriteBlobCommandMongo
extends|extends
name|AbstractCommand
argument_list|<
name|String
argument_list|>
block|{
specifier|private
specifier|final
name|MongoConnection
name|mongoConnection
decl_stmt|;
specifier|private
specifier|final
name|InputStream
name|is
decl_stmt|;
specifier|public
name|WriteBlobCommandMongo
parameter_list|(
name|MongoConnection
name|mongoConnection
parameter_list|,
name|InputStream
name|is
parameter_list|)
block|{
name|this
operator|.
name|mongoConnection
operator|=
name|mongoConnection
expr_stmt|;
name|this
operator|.
name|is
operator|=
name|is
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|saveBlob
argument_list|()
return|;
block|}
specifier|private
name|String
name|saveBlob
parameter_list|()
throws|throws
name|IOException
block|{
name|GridFS
name|gridFS
init|=
name|mongoConnection
operator|.
name|getGridFS
argument_list|()
decl_stmt|;
name|BufferedInputStream
name|bis
init|=
operator|new
name|BufferedInputStream
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|String
name|md5
init|=
name|calculateMd5
argument_list|(
name|bis
argument_list|)
decl_stmt|;
name|GridFSDBFile
name|gridFile
init|=
name|gridFS
operator|.
name|findOne
argument_list|(
operator|new
name|BasicDBObject
argument_list|(
literal|"md5"
argument_list|,
name|md5
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|gridFile
operator|!=
literal|null
condition|)
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|md5
return|;
block|}
name|GridFSInputFile
name|gridFSInputFile
init|=
name|gridFS
operator|.
name|createFile
argument_list|(
name|bis
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|gridFSInputFile
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
name|gridFSInputFile
operator|.
name|getMD5
argument_list|()
return|;
block|}
specifier|private
name|String
name|calculateMd5
parameter_list|(
name|BufferedInputStream
name|bis
parameter_list|)
throws|throws
name|IOException
block|{
name|bis
operator|.
name|mark
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|String
name|md5
init|=
name|DigestUtils
operator|.
name|md5Hex
argument_list|(
name|bis
argument_list|)
decl_stmt|;
name|bis
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|md5
return|;
block|}
block|}
end_class

end_unit

