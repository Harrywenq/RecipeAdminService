import { inject, Injectable } from '@angular/core';
import { TokenService } from '../service/token.service';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
} from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard {
  constructor(private tokenService: TokenService, private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    const isTokenExpired = this.tokenService.isTokenExpired();
    const userId = this.tokenService.getUserId();
    const isUserIdValid = userId !== null && userId.length > 0;
    debugger;
    if (!isTokenExpired && isUserIdValid) {
      return true;
    } else {
      //neu khong authenticated, co the redirect hoac tra ve urltree khac
      // ex tra ve login client
      this.router.navigate(['/login']);
      return false;
    }
  }
}

export const AuthGuardFn: CanActivateFn = (
  next: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
): boolean => {
  debugger;
  return inject(AuthGuard).canActivate(next, state);
};
