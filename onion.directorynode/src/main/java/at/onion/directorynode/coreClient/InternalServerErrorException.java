package at.onion.directorynode.coreClient;

public class InternalServerErrorException extends DirectoryNodeClientException {

		public InternalServerErrorException(){
			super();
		}
		
		public InternalServerErrorException(String msg){
			super(msg);
		}
		
		public InternalServerErrorException(Throwable cause){
			super(cause);
		}
		
		public InternalServerErrorException(String msg, Throwable cause){
			super(msg, cause);
		}
}
