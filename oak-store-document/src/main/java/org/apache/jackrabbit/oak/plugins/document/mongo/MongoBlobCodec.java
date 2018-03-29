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
name|mongo
package|;
end_package

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|BsonReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|BsonString
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|BsonValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|BsonWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|codecs
operator|.
name|Codec
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|codecs
operator|.
name|CollectibleCodec
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|codecs
operator|.
name|DecoderContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|codecs
operator|.
name|DocumentCodec
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|codecs
operator|.
name|EncoderContext
import|;
end_import

begin_class
class|class
name|MongoBlobCodec
implements|implements
name|CollectibleCodec
argument_list|<
name|MongoBlob
argument_list|>
block|{
specifier|private
specifier|final
name|Codec
argument_list|<
name|Document
argument_list|>
name|documentCodec
init|=
operator|new
name|DocumentCodec
argument_list|()
decl_stmt|;
name|MongoBlobCodec
parameter_list|()
block|{     }
annotation|@
name|Override
specifier|public
name|MongoBlob
name|generateIdIfAbsentFromDocument
parameter_list|(
name|MongoBlob
name|document
parameter_list|)
block|{
if|if
condition|(
operator|!
name|documentHasId
argument_list|(
name|document
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"MongoBlob must not have generated id"
argument_list|)
throw|;
block|}
return|return
name|document
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|documentHasId
parameter_list|(
name|MongoBlob
name|document
parameter_list|)
block|{
return|return
name|document
operator|.
name|getId
argument_list|()
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|BsonValue
name|getDocumentId
parameter_list|(
name|MongoBlob
name|document
parameter_list|)
block|{
if|if
condition|(
operator|!
name|documentHasId
argument_list|(
name|document
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"MongoBlob does not have an id"
argument_list|)
throw|;
block|}
return|return
operator|new
name|BsonString
argument_list|(
name|document
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|MongoBlob
name|decode
parameter_list|(
name|BsonReader
name|reader
parameter_list|,
name|DecoderContext
name|decoderContext
parameter_list|)
block|{
name|Document
name|doc
init|=
name|documentCodec
operator|.
name|decode
argument_list|(
name|reader
argument_list|,
name|decoderContext
argument_list|)
decl_stmt|;
return|return
name|MongoBlob
operator|.
name|fromDocument
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|encode
parameter_list|(
name|BsonWriter
name|writer
parameter_list|,
name|MongoBlob
name|value
parameter_list|,
name|EncoderContext
name|encoderContext
parameter_list|)
block|{
name|Document
name|doc
init|=
name|value
operator|.
name|asDocument
argument_list|()
decl_stmt|;
name|documentCodec
operator|.
name|encode
argument_list|(
name|writer
argument_list|,
name|doc
argument_list|,
name|encoderContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Class
argument_list|<
name|MongoBlob
argument_list|>
name|getEncoderClass
parameter_list|()
block|{
return|return
name|MongoBlob
operator|.
name|class
return|;
block|}
block|}
end_class

end_unit

