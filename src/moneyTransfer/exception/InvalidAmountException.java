package moneyTransfer.exception;

/**
 * 无效金额异常
 * 当金额为负数或为零时抛出
 */
public class InvalidAmountException extends Exception {
    
    public InvalidAmountException(String message) {
        super(message);
    }
    
    public InvalidAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}
