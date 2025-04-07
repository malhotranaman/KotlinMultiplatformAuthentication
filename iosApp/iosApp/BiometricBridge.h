#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface BiometricAuthIOS : NSObject

- (BOOL)isBiometricAvailable;
- (void)authenticateWithTitle:(NSString *)title
        subtitle:(NSString *)subtitle
        cancelText:(NSString *)cancelText
        completion:(void (^)(NSString *result))completion;
- (NSString *)getBiometricType;

@end

NS_ASSUME_NONNULL_END